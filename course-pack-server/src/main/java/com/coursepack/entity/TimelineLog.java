package com.coursepack.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 最新动态时间线（审核/教师状态操作写入；看板"最新动态"数据源），对应表 timeline_log
 */
@Data
@TableName("timeline_log")
public class TimelineLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 操作类型（如 TEACHER_APPROVED / AUDIT_PASSED） */
    private String action;

    /** 动态文案，如"教师 李锋 已通过审核" */
    private String content;

    /** 操作人 users.id */
    private Long operatorId;

    private LocalDateTime createdAt;

    @TableLogic
    private Integer deleted;
}