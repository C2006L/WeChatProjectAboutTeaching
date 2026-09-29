package com.coursepack.dto.admin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel("驳回请求")
public class RejectRequest {

    @ApiModelProperty(value = "驳回原因", required = true)
    @NotBlank(message = "驳回原因不能为空")
    private String reason;
}