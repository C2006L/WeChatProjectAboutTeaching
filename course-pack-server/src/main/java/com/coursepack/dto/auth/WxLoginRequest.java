package com.coursepack.dto.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 微信登录请求：wx.login() 获取的临时 code
 */
@Data
@ApiModel("微信登录请求")
public class WxLoginRequest {

    @ApiModelProperty(value = "wx.login() 返回的临时授权码", required = true)
    @NotBlank(message = "code 不能为空")
    private String code;
}
