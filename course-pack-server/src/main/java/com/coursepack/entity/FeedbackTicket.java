package com.coursepack.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 问答工单（教师提问 → 管理员回复，列表流）
 */
@Data
@TableName("feedback_tickets")
public class FeedbackTicket {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String title;

    private String content;

    /** 身体与安全 / 人际关系 / 成长变化 / 其他 */
    private String category;

    /** PENDING | REPLIED */
    private String status;

    private String replyContent;

    private LocalDateTime replyAt;

    private LocalDateTime createdAt;
}
