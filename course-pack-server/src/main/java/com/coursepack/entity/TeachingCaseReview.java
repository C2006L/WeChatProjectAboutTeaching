package com.coursepack.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 教学案例审核记录（四环节 EDUCATION|PSYCHOLOGY|SOCIOLOGY|PRINCIPAL）
 */
@Data
@TableName("teaching_case_reviews")
public class TeachingCaseReview {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long caseId;

    private String stage;

    private String comment;

    private String reviewer;

    private LocalDateTime reviewedAt;
}
