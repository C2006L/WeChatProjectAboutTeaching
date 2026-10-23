package com.coursepack.dto.feedback;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 管理员回复请求
 */
@Data
@ApiModel("问答工单回复")
public class TicketReplyRequest {

    @ApiModelProperty("回复内容")
    @NotBlank(message = "回复内容不能为空")
    @Size(max = 1024, message = "回复内容过长")
    private String content;
}
