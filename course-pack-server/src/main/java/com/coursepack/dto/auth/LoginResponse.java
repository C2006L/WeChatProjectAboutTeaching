package com.coursepack.dto.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应：JWT（携带 role + status）+ 当前用户信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("登录响应")
public class LoginResponse {

    @ApiModelProperty("JWT，7 天过期，携带 role+status")
    private String token;

    @ApiModelProperty("当前用户信息")
    private UserVO user;
}