package com.coursepack.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.coursepack.auth.AuthContext;
import com.coursepack.common.BusinessException;
import com.coursepack.common.ErrorCode;
import com.coursepack.dto.audit.CourseAuditVO;
import com.coursepack.entity.CourseAudit;
import com.coursepack.mapper.CourseAuditMapper;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 课程审核：按审核员 auditField 过滤待审队列；批准/驳回写时间线。
 */
@Service
public class CourseAuditService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final CourseAuditMapper auditMapper;
    private final TimelineService timelineService;

    public CourseAuditService(CourseAuditMapper auditMapper, TimelineService timelineService) {
        this.auditMapper = auditMapper;
        this.timelineService = timelineService;
    }

    /** 当前审核员可审的待审队列（audit_field 匹配 + PENDING） */
    public List<CourseAuditVO> queue() {
        String field = AuthContext.get() == null ? null : AuthContext.get().getAuditField();
        List<CourseAudit> list = auditMapper.selectList(
                Wrappers.<CourseAudit>lambdaQuery()
                        .eq(CourseAudit::getStatus, "PENDING")
                        .eq(field != null, CourseAudit::getAuditField, field)
                        .orderByAsc(CourseAudit::getSubmitTime));
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    public void approve(Long id) {
        CourseAudit a = auditOrFail(id);
        if (!"PENDING".equals(a.getStatus())) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "该课程已处理过");
        }
        a.setStatus("APPROVED");
        a.setProgress(100);
        a.setAuditorId(AuthContext.userId());
        auditMapper.updateById(a);
        timelineService.log("AUDIT_PASSED", "课程《" + a.getTitle() + "》审核通过", AuthContext.userId());
    }

    public void reject(Long id, String reason) {
        CourseAudit a = auditOrFail(id);
        if (!"PENDING".equals(a.getStatus())) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "该课程已处理过");
        }
        a.setStatus("REJECTED");
        a.setRejectReason(reason);
        a.setAuditorId(AuthContext.userId());
        auditMapper.updateById(a);
        timelineService.log("AUDIT_REJECTED", "课程《" + a.getTitle() + "》被驳回：" + reason, AuthContext.userId());
    }

    private CourseAudit auditOrFail(Long id) {
        CourseAudit a = auditMapper.selectById(id);
        if (a == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "审核记录不存在");
        }
        return a;
    }

    private CourseAuditVO toVO(CourseAudit a) {
        CourseAuditVO vo = new CourseAuditVO();
        vo.setId(a.getId());
        vo.setCourseId(a.getCourseId());
        vo.setTitle(a.getTitle());
        vo.setAuditField(a.getAuditField());
        vo.setApplicant(a.getApplicant());
        vo.setSubmitTime(a.getSubmitTime() == null ? null : a.getSubmitTime().format(FMT));
        vo.setProgress(a.getProgress());
        vo.setStatus(a.getStatus());
        vo.setRejectReason(a.getRejectReason());
        return vo;
    }
}