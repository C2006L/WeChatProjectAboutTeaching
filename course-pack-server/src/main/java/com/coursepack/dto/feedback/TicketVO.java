package com.coursepack.dto.feedback;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 问答工单 VO（客户端与 Web 管理端共用；对齐 FeedbackTicket 类型）
 */
@Data
@ApiModel("问答工单")
public class TicketVO {

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("提问教师（users.name）")
    private String teacher;

    @ApiModelProperty("所属队伍（批次J4 建立关联前：未分配）")
    private String team;

    @ApiModelProperty("提问时间 yyyy-MM-dd HH:mm")
    private String time;

    @ApiModelProperty("PENDING | REPLIED")
    private String status;

    @ApiModelProperty("工单标题")
    private String title;

    @ApiModelProperty("问题描述")
    private String content;

    @ApiModelProperty("问题分类")
    private String category;

    @ApiModelProperty("回复信息（REPLIED 时有值）")
    private Reply reply;

    @Data
    @ApiModel("管理员回复")
    public static class Reply {
        @ApiModelProperty("回复内容")
        private String content;
        @ApiModelProperty("回复管理员")
        private String admin;
        @ApiModelProperty("回复时间 yyyy-MM-dd HH:mm")
        private String time;
    }
}
