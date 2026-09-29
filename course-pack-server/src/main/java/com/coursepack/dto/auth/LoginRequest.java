package com.coursepack.dto.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 统一登录请求（小程序与 Web 共用同一账号密码）
 */
@Data
@ApiModel("登录请求")
public class LoginRequest {

    @ApiModelProperty(value = "登录账号", required = true)
    @NotBlank(message = "账号不能为空")
    private String username;

    @ApiModelProperty(value = "密码", required = true)
    @NotBlank(message = "密码不能为空")
    private String password;
}