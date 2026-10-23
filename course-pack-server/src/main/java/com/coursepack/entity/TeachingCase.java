package com.coursepack.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 教学案例（批次J：复用四环节审核流转）
 */
@Data
@TableName("teaching_cases")
public class TeachingCase {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long uploaderId;

    private String title;

    /** 小学1-3年级 | 小学4-6年级 */
    private String grade;

    /** 主题中文标签 */
    private String theme;

    private String courseTitle;

    private String summary;

    private String content;

    private String reflection;

    /** DRAFT | PENDING | APPROVED | REJECTED */
    private String status;

    private Integer featured;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
