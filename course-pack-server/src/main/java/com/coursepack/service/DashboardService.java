package com.coursepack.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.coursepack.dto.admin.DashboardVO;
import com.coursepack.dto.admin.TimelineLogVO;
import com.coursepack.converter.TimelineLogConverter;
import com.coursepack.entity.Course;
import com.coursepack.entity.Favorite;
import com.coursepack.entity.User;
import com.coursepack.mapper.CourseAuditMapper;
import com.coursepack.mapper.CourseMapper;
import com.coursepack.mapper.FavoriteMapper;
import com.coursepack.mapper.UserMapper;
import com.coursepack.entity.CourseAudit;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理端看板聚合（批次H）：只计算后端真实可求的部分，
 * 队伍/培训/案例域（批次J）由前端 mock 兜底。
 */
@Service
public class DashboardService {

    private final UserMapper userMapper;
    private final CourseMapper courseMapper;
    private final FavoriteMapper favoriteMapper;
    private final CourseAuditMapper courseAuditMapper;
    private final TimelineService timelineService;
    private final TimelineLogConverter timelineConverter;

    public DashboardService(UserMapper userMapper, CourseMapper courseMapper,
                            FavoriteMapper favoriteMapper, CourseAuditMapper courseAuditMapper,
                            TimelineService timelineService, TimelineLogConverter timelineConverter) {
        this.userMapper = userMapper;
        this.courseMapper = courseMapper;
        this.favoriteMapper = favoriteMapper;
        this.courseAuditMapper = courseAuditMapper;
        this.timelineService = timelineService;
        this.timelineConverter = timelineConverter;
    }

    public DashboardVO build() {
        DashboardVO vo = new DashboardVO();

        long teacherPending = userMapper.selectCount(Wrappers.<User>lambdaQuery()
                .eq(User::getRole, "TEACHER").eq(User::getStatus, "PENDING"));
        long auditPending = courseAuditMapper.selectCount(Wrappers.<CourseAudit>lambdaQuery()
                .eq(CourseAudit::getStatus, "PENDING"));
        vo.setPendingTeachers((int) teacherPending);
        vo.setPendingAudits((int) auditPending);
        vo.setTodoCount((int) (teacherPending + auditPending));

        long teacherCount = userMapper.selectCount(Wrappers.<User>lambdaQuery()
                .eq(User::getRole, "TEACHER"));
        vo.setTeacherCount((int) teacherCount);

        long online = courseMapper.selectCount(Wrappers.<Course>lambdaQuery().eq(Course::getStatus, 1));
        vo.setCourseOnlineCount((int) online);

        long favCount = favoriteMapper.selectCount(Wrappers.emptyWrapper());
        vo.setFavoriteCount((int) favCount);

        vo.setGrowth(teacherGrowth());
        vo.setThemes(themeDistribution());
        vo.setActivities(toActivities());
        return vo;
    }

    /** 近 6 个月教师累计数（按 users.created_at 月 bucket） */
    private List<DashboardVO.GrowthPoint> teacherGrowth() {
        LocalDate today = LocalDate.now();
        List<LocalDate> monthStarts = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            monthStarts.add(today.minusMonths(i).withDayOfMonth(1));
        }
        List<LocalDateTime> created = userMapper.selectList(Wrappers.<User>lambdaQuery()
                        .eq(User::getRole, "TEACHER"))
                .stream().map(User::getCreatedAt).filter(d -> d != null)
                .collect(Collectors.toList());

        List<DashboardVO.GrowthPoint> points = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM");
        for (LocalDate start : monthStarts) {
            // 累计：该月月末（下一月 1 日零点前）的教师总数
            LocalDate next = start.plusMonths(1);
            long cum = created.stream().filter(d -> d.isBefore(next.atStartOfDay())).count();
            DashboardVO.GrowthPoint p = new DashboardVO.GrowthPoint();
            p.setMonth(start.format(fmt));
            p.setTeachers((int) cum);
            points.add(p);
        }
        return points;
    }

    /** 已上线课程按主题分布（小数据量，内存聚合） */
    private List<DashboardVO.ThemeCount> themeDistribution() {
        List<Course> online = courseMapper.selectList(Wrappers.<Course>lambdaQuery().eq(Course::getStatus, 1));
        Map<String, Integer> byLabel = new HashMap<>();
        for (Course c : online) {
            byLabel.merge(CourseService.themeLabel(c.getTheme()), 1, Integer::sum);
        }
        int total = online.size();
        // 固定主题顺序（对齐前端五主题）
        List<String> order = List.of("身体与安全", "家庭与合作", "人际与尊重", "成长与变化", "综合主题");
        List<DashboardVO.ThemeCount> list = new ArrayList<>();
        for (String label : order) {
            int v = byLabel.getOrDefault(label, 0);
            if (v == 0) {
                continue;
            }
            DashboardVO.ThemeCount t = new DashboardVO.ThemeCount();
            t.setTheme(label);
            t.setValue(v);
            t.setPercent(total == 0 ? 0 : Math.round(v * 100.0f / total));
            list.add(t);
        }
        return list;
    }

    private List<TimelineLogVO> toActivities() {
        return timelineConverter.toVOList(timelineService.latest(10));
    }
}
