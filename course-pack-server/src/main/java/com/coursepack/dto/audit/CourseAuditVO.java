package com.coursepack.dto.audit;

import lombok.Data;

/**
 * 课程审核记录（AUDITOR 审核中心）
 */
@Data
public class CourseAuditVO {
    private Long id;
    private Long courseId;
    private String title;
    private String auditField;
    private String applicant;
    private String submitTime;
    private Integer progress;
    private String status;
    private String rejectReason;
}