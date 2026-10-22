package com.coursepack.service;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.coursepack.dto.course.CourseMaterialVO;
import com.coursepack.dto.course.CourseStep;
import com.coursepack.dto.course.CourseUpsertRequest;
import com.coursepack.dto.course.CourseVO;
import com.coursepack.dto.course.MaterialsVO;
import com.coursepack.entity.Course;
import com.coursepack.entity.CourseMaterial;
import com.coursepack.entity.Favorite;
import com.coursepack.common.BusinessException;
import com.coursepack.common.ErrorCode;
import com.coursepack.mapper.CourseMapper;
import com.coursepack.mapper.CourseMaterialMapper;
import com.coursepack.mapper.FavoriteMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 课程域（批次H）：管理端 CRUD + 小程序端浏览与收藏。
 * 数组字段以 JSON 文本存于 courses 表，进出统一在此转换。
 */
@Service
public class CourseService {

    /** 主题 → 中文名（对齐前端 coverMap/接口规范） */
    private static final Map<String, String> THEME_LABELS = Map.of(
            "body", "身体与安全",
            "family", "家庭与合作",
            "respect", "人际与尊重",
            "growth", "成长与变化",
            "general", "综合主题");

    private final CourseMapper courseMapper;
    private final CourseMaterialMapper materialMapper;
    private final FavoriteMapper favoriteMapper;

    public CourseService(CourseMapper courseMapper, CourseMaterialMapper materialMapper,
                         FavoriteMapper favoriteMapper) {
        this.courseMapper = courseMapper;
        this.materialMapper = materialMapper;
        this.favoriteMapper = favoriteMapper;
    }

    public static String themeLabel(String theme) {
        return THEME_LABELS.getOrDefault(theme, "综合主题");
    }

    /* ================= 客户端 ================= */

    /** 已上架课程全量（前端筛选，对齐 mock 行为） */
    public List<CourseVO> listOnline() {
        return courseMapper.selectList(Wrappers.<Course>lambdaQuery()
                        .eq(Course::getStatus, 1).orderByAsc(Course::getId))
                .stream().map(this::toVO).collect(Collectors.toList());
    }

    public CourseVO onlineDetail(Long id) {
        Course c = courseMapper.selectById(id);
        if (c == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "课程不存在或已删除");
        }
        if (c.getStatus() == null || c.getStatus() != 1) {
            throw new BusinessException(ErrorCode.COURSE_OFFLINE);
        }
        return toVO(c);
    }

    public List<CourseMaterialVO> materials(Long courseId) {
        return materialMapper.selectList(Wrappers.<CourseMaterial>lambdaQuery()
                        .eq(CourseMaterial::getCourseId, courseId).orderByAsc(CourseMaterial::getId))
                .stream().map(this::toMaterialVO).collect(Collectors.toList());
    }

    /** 收藏 toggle：已收藏则取消，未收藏则加入；返回切换后的状态 */
    public boolean toggleFavorite(Long userId, Long courseId) {
        courseMapper.selectById(courseId);
        if (courseMapper.selectById(courseId) == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "课程不存在或已删除");
        }
        Favorite fav = favoriteMapper.selectOne(Wrappers.<Favorite>lambdaQuery()
                .eq(Favorite::getUserId, userId).eq(Favorite::getCourseId, courseId));
        if (fav != null) {
            favoriteMapper.deleteById(fav.getId());
            return false;
        }
        Favorite f = new Favorite();
        f.setUserId(userId);
        f.setCourseId(courseId);
        f.setCreatedAt(LocalDateTime.now());
        favoriteMapper.insert(f);
        return true;
    }

    /** 我的收藏（上架课程，按收藏时间倒序） */
    public List<CourseVO> favoritesOf(Long userId) {
        List<Favorite> favs = favoriteMapper.selectList(Wrappers.<Favorite>lambdaQuery()
                .eq(Favorite::getUserId, userId).orderByDesc(Favorite::getId));
        if (favs.isEmpty()) {
            return List.of();
        }
        List<Long> ids = favs.stream().map(Favorite::getCourseId).collect(Collectors.toList());
        Map<Long, Course> byId = courseMapper.selectBatchIds(ids).stream()
                .collect(Collectors.toMap(Course::getId, c -> c));
        return favs.stream()
                .filter(f -> byId.containsKey(f.getCourseId()))
                .map(f -> toVO(byId.get(f.getCourseId())))
                .collect(Collectors.toList());
    }

    /* ================= 管理端 ================= */

    public Page<CourseVO> adminList(long page, long size, String keyword, Integer status) {
        QueryWrapper<Course> qw = new QueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            qw.like("title", keyword);
        }
        if (status != null) {
            qw.eq("status", status);
        }
        qw.orderByDesc("id");
        Page<Course> p = courseMapper.selectPage(new Page<>(page, size), qw);
        Page<CourseVO> voPage = new Page<>(p.getCurrent(), p.getSize(), p.getTotal());
        voPage.setPages(p.getPages());
        voPage.setRecords(p.getRecords().stream().map(this::toVO).collect(Collectors.toList()));
        return voPage;
    }

    public CourseVO adminGet(Long id) {
        Course c = courseMapper.selectById(id);
        if (c == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "课程不存在或已删除");
        }
        return toVO(c);
    }

    @Transactional
    public CourseVO create(CourseUpsertRequest req) {
        Course c = new Course();
        applyUpsert(c, req);
        courseMapper.insert(c);
        regenerateMaterials(c.getId(), req.getMaterials(), req.getTitle());
        return toVO(courseMapper.selectById(c.getId()));
    }

    @Transactional
    public CourseVO update(Long id, CourseUpsertRequest req) {
        Course existing = courseMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "课程不存在或已删除");
        }
        applyUpsert(existing, req);
        courseMapper.updateById(existing);
        regenerateMaterials(id, req.getMaterials(), req.getTitle());
        return toVO(courseMapper.selectById(id));
    }

    @Transactional
    public void delete(Long id) {
        Course c = courseMapper.selectById(id);
        if (c == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "课程不存在或已删除");
        }
        courseMapper.deleteById(id);
    }

    /* ================= 转换与 JSON ================= */

    private void applyUpsert(Course c, CourseUpsertRequest req) {
        c.setTitle(req.getTitle());
        c.setTheme(req.getTheme());
        c.setGrade(req.getGrade());
        c.setDuration(req.getDuration());
        c.setVersion(req.getVersion());
        c.setSummary(req.getSummary());
        c.setCover(req.getCover());
        c.setStatus(req.getStatus() == null ? 1 : req.getStatus());
        c.setObjectivesJson(req.getObjectives() == null ? null : JSONUtil.toJsonStr(req.getObjectives()));
        c.setValuesJson(req.getValues() == null ? null : JSONUtil.toJsonStr(req.getValues()));
        c.setStepsJson(req.getSteps() == null ? null : JSONUtil.toJsonStr(req.getSteps()));
        c.setSensitiveQaJson(req.getSensitiveQa() == null ? null : JSONUtil.toJsonStr(req.getSensitiveQa()));
    }

    /** 按数量重建材料行（管理端以数量提交材料；url 待 OSS 批次补齐） */
    private void regenerateMaterials(Long courseId, MaterialsVO counts, String title) {
        materialMapper.delete(Wrappers.<CourseMaterial>lambdaQuery()
                .eq(CourseMaterial::getCourseId, courseId));
        if (counts == null) {
            return;
        }
        List<CourseMaterial> rows = new ArrayList<>();
        addRows(rows, courseId, "plan", counts.getPlan(), "《" + title + "》教案");
        addRows(rows, courseId, "slides", counts.getSlides(), "《" + title + "》课件");
        addRows(rows, courseId, "toolkit", counts.getToolkit(), "《" + title + "》教具集");
        addRows(rows, courseId, "video", counts.getVideo(), "《" + title + "》视频分集");
        for (CourseMaterial m : rows) {
            materialMapper.insert(m);
        }
    }

    private void addRows(List<CourseMaterial> rows, Long courseId, String type, Integer n, String baseName) {
        for (int i = 1; n != null && i <= n; i++) {
            CourseMaterial m = new CourseMaterial();
            m.setCourseId(courseId);
            m.setType(type);
            m.setName(n == 1 ? baseName : baseName + (type.equals("plan") || type.equals("slides")
                    ? "（第 " + i + " 学段）" : (type.equals("video") ? " · 第 " + i + " 集" : " · " + i)));
            m.setCreatedAt(LocalDateTime.now());
            rows.add(m);
        }
    }

    private CourseVO toVO(Course c) {
        CourseVO vo = new CourseVO();
        vo.setId(c.getId());
        vo.setTitle(c.getTitle());
        vo.setTheme(c.getTheme());
        vo.setThemeLabel(themeLabel(c.getTheme()));
        vo.setGrade(c.getGrade());
        vo.setDuration(c.getDuration());
        vo.setVersion(c.getVersion());
        vo.setSummary(c.getSummary());
        vo.setCover(c.getCover());
        vo.setObjectives(parseStringList(c.getObjectivesJson()));
        vo.setValues(parseStringList(c.getValuesJson()));
        vo.setSensitiveQa(parseLongList(c.getSensitiveQaJson()));
        vo.setSteps(parseSteps(c.getStepsJson()));
        vo.setStatus(c.getStatus());
        vo.setUpdatedAt(c.getUpdatedAt());

        // 材料数量统计
        List<CourseMaterial> ms = materialMapper.selectList(Wrappers.<CourseMaterial>lambdaQuery()
                .eq(CourseMaterial::getCourseId, c.getId()));
        int plan = 0, slides = 0, toolkit = 0, video = 0;
        for (CourseMaterial m : ms) {
            switch (m.getType()) {
                case "plan": plan++; break;
                case "slides": slides++; break;
                case "toolkit": toolkit++; break;
                case "video": video++; break;
                default: break;
            }
        }
        vo.setMaterials(new MaterialsVO(plan, slides, toolkit, video));
        return vo;
    }

    private CourseMaterialVO toMaterialVO(CourseMaterial m) {
        CourseMaterialVO vo = new CourseMaterialVO();
        vo.setId(m.getId());
        vo.setType(m.getType());
        vo.setName(m.getName());
        vo.setUrl(m.getUrl());
        vo.setSize(m.getSize());
        return vo;
    }

    private List<String> parseStringList(String json) {
        if (!StringUtils.hasText(json)) {
            return List.of();
        }
        try {
            return JSONUtil.toList(JSONUtil.parseArray(json), String.class);
        } catch (Exception e) {
            return List.of();
        }
    }

    private List<Long> parseLongList(String json) {
        if (!StringUtils.hasText(json)) {
            return List.of();
        }
        try {
            return JSONUtil.toList(JSONUtil.parseArray(json), Long.class);
        } catch (Exception e) {
            return List.of();
        }
    }

    private List<CourseStep> parseSteps(String json) {
        if (!StringUtils.hasText(json)) {
            return List.of();
        }
        try {
            return JSONUtil.toList(JSONUtil.parseArray(json), CourseStep.class);
        } catch (Exception e) {
            return List.of();
        }
    }
}
