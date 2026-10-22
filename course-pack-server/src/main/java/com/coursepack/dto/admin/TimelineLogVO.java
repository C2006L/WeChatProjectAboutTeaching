package com.coursepack.dto.admin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 最新动态时间线条目（看板"最新动态"数据源）
 */
@Data
@ApiModel("最新动态")
public class TimelineLogVO {

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("操作类型，如 TEACHER_APPROVED / AUDIT_PASSED")
    private String action;

    @ApiModelProperty("动态文案")
    private String content;

    @ApiModelProperty("操作人 users.id（可为空）")
    private Long operatorId;

    @ApiModelProperty("发生时间")
    private LocalDateTime createdAt;
}
