package com.coursepack.dto.dashboard;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 审核意见记录（对齐管理端 AuditComment）
 */
@Data
@ApiModel("审核意见")
public class AuditCommentVO {

    @ApiModelProperty("环节 key")
    private String stageKey;

    @ApiModelProperty("环节中文名")
    private String stageLabel;

    @ApiModelProperty("意见内容")
    private String content;

    @ApiModelProperty("审核人展示名")
    private String reviewer;

    @ApiModelProperty("时间 yyyy-MM-dd HH:mm")
    private String time;
}
