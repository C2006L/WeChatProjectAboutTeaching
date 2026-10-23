package com.coursepack.dto.caseitem;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 教学案例 VO（客户端形态，对齐 TeachingCase 类型）
 */
@Data
@ApiModel("教学案例")
public class CaseVO {

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("学段 小学1-3年级|小学4-6年级")
    private String grade;

    @ApiModelProperty("主题（中文标签）")
    private String theme;

    @ApiModelProperty("关联课程")
    private String courseTitle;

    @ApiModelProperty("摘要")
    private String summary;

    @ApiModelProperty("案例正文")
    private String content;

    @ApiModelProperty("教学反思")
    private String reflection;

    @ApiModelProperty("作者名（users.name）")
    private String author;

    @ApiModelProperty("队伍（批次J4 前未分配升华）")
    private String team;

    @ApiModelProperty("DRAFT|PENDING|APPROVED|REJECTED")
    private String status;

    @ApiModelProperty("精选")
    private Boolean featured;

    @ApiModelProperty("是否我的案例")
    private Boolean isMine;

    @ApiModelProperty("创建 yyyy-MM-dd")
    private String createdAt;

    @ApiModelProperty("更新 yyyy-MM-dd")
    private String updatedAt;

    @ApiModelProperty("驳回意见（REJECTED 时）")
    private String reviewComment;
}
