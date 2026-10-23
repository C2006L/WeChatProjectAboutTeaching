package com.coursepack.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.coursepack.common.BusinessException;
import com.coursepack.common.ErrorCode;
import com.coursepack.dto.caseitem.CaseAdminVO;
import com.coursepack.dto.caseitem.CaseUpsertRequest;
import com.coursepack.dto.caseitem.CaseVO;
import com.coursepack.dto.dashboard.AuditCommentVO;
import com.coursepack.dto.dashboard.AuditStageVO;
import com.coursepack.entity.TeachingCase;
import com.coursepack.entity.TeachingCaseReview;
import com.coursepack.entity.User;
import com.coursepack.mapper.TeachingCaseMapper;
import com.coursepack.mapper.TeachingCaseReviewMapper;
import com.coursepack.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 教学案例服务（批次J）：教师上传/草稿、公共案例库、管理端列表与精选。
 */
@Service
public class CaseService {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /** 四环节固定顺序（案例流转仅展示） */
    private static final String[] STAGES = { "EDUCATION", "PSYCHOLOGY", "SOCIOLOGY", "PRINCIPAL" };
    private static final String[] STAGE_LABELS = { "教育审核", "心理审核", "社会审核", "校长终审" };
    private static final String[] STAGE_KEYS = { "education", "psychology", "sociology", "principal" };

    private final TeachingCaseMapper caseMapper;
    private final TeachingCaseReviewMapper reviewMapper;
    private final UserMapper userMapper;
    private final TimelineService timelineService;

    public CaseService(TeachingCaseMapper caseMapper, TeachingCaseReviewMapper reviewMapper,
                       UserMapper userMapper, TimelineService timelineService) {
        this.caseMapper = caseMapper;
        this.reviewMapper = reviewMapper;
        this.userMapper = userMapper;
        this.timelineService = timelineService;
    }

    /** 公共案例库（APPROVED，更新时间倒序） */
    public List<CaseVO> publicList() {
        List<TeachingCase> list = caseMapper.selectList(Wrappers.<TeachingCase>lambdaQuery()
                .eq(TeachingCase::getStatus, "APPROVED")
                .orderByDesc(TeachingCase::getUpdatedAt));
        return list.stream().map(c -> toVO(c, false, null)).collect(Collectors.toList());
    }

    /** 我的案例（全部状态，更新时间倒序） */
    public List<CaseVO> mine(Long userId) {
        List<TeachingCase> list = caseMapper.selectList(Wrappers.<TeachingCase>lambdaQuery()
                .eq(TeachingCase::getUploaderId, userId)
                .orderByDesc(TeachingCase::getUpdatedAt));
        return list.stream().map(c -> toVO(c, true, null)).collect(Collectors.toList());
    }

    /** 详情：已通过或本人可见 */
    public CaseVO detail(Long id, Long userId) {
        TeachingCase c = caseMapper.selectById(id);
        if (c == null || !"APPROVED".equals(c.getStatus()) && !c.getUploaderId().equals(userId)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        String reviewComment = latestRejection(c.getUploaderId(), c);
        return toVO(c, c.getUploaderId().equals(userId), reviewComment);
    }

    /** 教师保存草稿 / 提交审核（DRAFT | PENDING） */
    @Transactional
    public CaseVO upsert(Long userId, Long caseId, CaseUpsertRequest req, boolean userLog) {
        String action = "DRAFT".equals(req.getAction()) ? "DRAFT" : "PENDING";
        TeachingCase c;
        if (caseId != null) {
            c = caseMapper.selectById(caseId);
            if (c == null || !c.getUploaderId().equals(userId)) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
            }
            c.setTitle(req.getTitle());
            c.setGrade(req.getGrade());
            c.setTheme(req.getTheme());
            c.setCourseTitle(req.getCourseTitle());
            c.setSummary(req.getSummary());
            c.setContent(req.getContent());
            c.setReflection(req.getReflection());
            // 再次提交：DRAFT→PENDING；已被驳回后重新提交仅回 PENDING
            c.setStatus(action);
            c.setUpdatedAt(LocalDateTime.now());
            caseMapper.updateById(c);
        } else {
            c = new TeachingCase();
            c.setUploaderId(userId);
            c.setTitle(req.getTitle());
            c.setGrade(req.getGrade());
            c.setTheme(req.getTheme());
            c.setCourseTitle(req.getCourseTitle());
            c.setSummary(req.getSummary());
            c.setContent(req.getContent());
            c.setReflection(req.getReflection());
            c.setStatus(action);
            c.setFeatured(0);
            c.setCreatedAt(LocalDateTime.now());
            c.setUpdatedAt(LocalDateTime.now());
            caseMapper.insert(c);
        }
        if (userLog && "PENDING".equals(action)) {
            String name = displayName(userId);
            timelineService.log("CASE_SUBMITTED", "教师「" + name + "」提交教学案例《" + c.getTitle() + "》",
                    userId);
        }
        return toVO(c, true, null);
    }

    /* ================= 管理端 ================= */

    /** 管理端全量（更新时间倒序） */
    public List<CaseAdminVO> adminList() {
        List<TeachingCase> list = caseMapper.selectList(Wrappers.<TeachingCase>lambdaQuery()
                .orderByDesc(TeachingCase::getUpdatedAt));
        Map<Long, List<TeachingCaseReview>> reviewsByCase = reviewMapper.selectList(
                        Wrappers.emptyWrapper()).stream()
                .collect(Collectors.groupingBy(TeachingCaseReview::getCaseId));
        Map<Long, String> names = namesOf(list);
        return list.stream()
                .map(c -> toAdminVO(c, names.getOrDefault(c.getUploaderId(), "教师"),
                        reviewsByCase.getOrDefault(c.getId(), List.of())))
                .collect(Collectors.toList());
    }

    /** 精选切换（写时间线），返回切换后的值 */
    @Transactional
    public boolean toggleFeatured(Long caseId, Long operatorId) {
        TeachingCase c = caseMapper.selectById(caseId);
        if (c == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        boolean next = c.getFeatured() == null || c.getFeatured() == 0;
        c.setFeatured(next ? 1 : 0);
        c.setUpdatedAt(LocalDateTime.now());
        caseMapper.updateById(c);
        timelineService.log("CASE_FEATURED",
                next ? "已将案例「" + c.getTitle() + "」标记为精选"
                        : "已取消案例「" + c.getTitle() + "」的精选标记", operatorId);
        return next;
    }

    /* ================= 映射 ================= */

    private CaseVO toVO(TeachingCase c, boolean isMine, String reviewComment) {
        CaseVO vo = new CaseVO();
        vo.setId(c.getId());
        vo.setTitle(c.getTitle());
        vo.setGrade(c.getGrade());
        vo.setTheme(c.getTheme());
        vo.setCourseTitle(c.getCourseTitle());
        vo.setSummary(c.getSummary());
        vo.setContent(c.getContent());
        vo.setReflection(c.getReflection());
        vo.setAuthor(displayName(c.getUploaderId()));
        vo.setTeam("未分配"); // 批次J4：队伍域建立后补齐
        vo.setStatus(c.getStatus());
        vo.setFeatured(c.getFeatured() != null && c.getFeatured() > 0);
        vo.setIsMine(isMine);
        vo.setCreatedAt(c.getCreatedAt() == null ? "" : DATE.format(c.getCreatedAt()));
        vo.setUpdatedAt(c.getUpdatedAt() == null ? "" : DATE.format(c.getUpdatedAt()));
        vo.setReviewComment(reviewComment);
        return vo;
    }

    private CaseAdminVO toAdminVO(TeachingCase c, String uploaderName, List<TeachingCaseReview> reviews) {
        CaseAdminVO vo = new CaseAdminVO();
        vo.setId(c.getId());
        vo.setTitle(c.getTitle());
        vo.setUploader(uploaderName);
        vo.setTeam("未分配");
        vo.setUploadedAt(c.getCreatedAt() == null ? "" : DATE.format(c.getCreatedAt()));
        boolean approved = "APPROVED".equals(c.getStatus());
        vo.setStatus(approved ? "APPROVED" : "PENDING");
        vo.setFeatured(c.getFeatured() != null && c.getFeatured() > 0);
        vo.setSummary(c.getSummary());

        Map<String, TeachingCaseReview> byStage = reviews.stream()
                .collect(Collectors.toMap(TeachingCaseReview::getStage, r -> r, (a, b) -> a));
        List<AuditStageVO> stages = new java.util.ArrayList<>();
        for (int i = 0; i < STAGES.length; i++) {
            AuditStageVO s = new AuditStageVO();
            s.setKey(STAGE_KEYS[i]);
            s.setLabel(STAGE_LABELS[i]);
            s.setDone(byStage.containsKey(STAGES[i]));
            stages.add(s);
        }
        vo.setStages(stages);

        vo.setComments(reviews.stream().map(r -> {
            AuditCommentVO cm = new AuditCommentVO();
            int idx = List.of(STAGES).indexOf(r.getStage());
            cm.setStageKey(idx >= 0 ? STAGE_KEYS[idx] : "education");
            cm.setStageLabel(idx >= 0 ? STAGE_LABELS[idx] : "教育审核");
            cm.setContent(r.getComment());
            cm.setReviewer(r.getReviewer());
            cm.setTime(r.getReviewedAt() == null ? "" : TIME.format(r.getReviewedAt()));
            return cm;
        }).collect(Collectors.toList()));
        return vo;
    }

    /** 最近一次驳回原因（案例域无驳回写路径前恒 null，seed 备用） */
    private String latestRejection(Long userId, TeachingCase c) {
        TeachingCaseReview r = reviewMapper.selectOne(Wrappers.<TeachingCaseReview>lambdaQuery()
                .eq(TeachingCaseReview::getCaseId, c.getId())
                .orderByDesc(TeachingCaseReview::getId)
                .last("LIMIT 1"));
        return r == null ? null : r.getComment();
    }

    private Map<Long, String> namesOf(List<TeachingCase> cases) {
        List<Long> ids = cases.stream().map(TeachingCase::getUploaderId).distinct()
                .collect(Collectors.toList());
        if (ids.isEmpty()) {
            return Map.of();
        }
        return userMapper.selectList(Wrappers.<User>lambdaQuery().in(User::getId, ids)).stream()
                .collect(Collectors.toMap(User::getId, u -> u.getName() == null ? "教师" : u.getName()));
    }

    private String displayName(Long userId) {
        User u = userMapper.selectById(userId);
        return u != null && u.getName() != null ? u.getName() : "教师";
    }
}
