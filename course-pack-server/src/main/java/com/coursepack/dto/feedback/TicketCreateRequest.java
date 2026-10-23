package com.coursepack.dto.feedback;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 教师提问请求
 */
@Data
@ApiModel("问答工单提问")
public class TicketCreateRequest {

    @ApiModelProperty("工单标题")
    @NotBlank(message = "标题不能为空")
    @Size(max = 64, message = "标题过长")
    private String title;

    @ApiModelProperty("问题描述")
    @NotBlank(message = "问题描述不能为空")
    @Size(max = 1024, message = "问题描述过长")
    private String content;

    @ApiModelProperty("分类 身体与安全|人际关系|成长变化|其他")
    private String category;
}
