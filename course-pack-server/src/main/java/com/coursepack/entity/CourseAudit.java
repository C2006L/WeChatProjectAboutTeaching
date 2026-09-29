package com.coursepack.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 课程审核（AUDITOR 专用；按 auditField 过滤待审队列）
 */
@Data
@TableName("course_audit")
public class CourseAudit {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联课程ID */
    private Long courseId;

    /** 课程标题 */
    private String title;

    /** 审核领域 EDU|PSY|SOC（同步申请课程的 auditField） */
    private String auditField;

    /** 申请人（教师姓名） */
    private String applicant;

    /** 提交时间 */
    private LocalDateTime submitTime;

    /** 审核员 users.id（批准后回填） */
    private Long auditorId;

    /** PENDING|APPROVED|REJECTED */
    private String status;

    /** 拟物进度：0-100（四阶段种子→发芽→长叶→小树） */
    private Integer progress;

    /** 驳回原因 */
    private String rejectReason;

    private LocalDateTime createdAt;

    @TableLogic
    private Integer deleted;
}