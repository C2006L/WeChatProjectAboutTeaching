package com.coursepack.controller;

import com.coursepack.auth.AuthContext;
import com.coursepack.common.Result;
import com.coursepack.dto.auth.LoginRequest;
import com.coursepack.dto.auth.LoginResponse;
import com.coursepack.dto.auth.UserVO;
import com.coursepack.dto.auth.WxLoginRequest;
import com.coursepack.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 统一认证：小程序与 Web 共用同一 /login。
 * 状态机：ACTIVE 放行；PENDING/REJECTED/DISABLED 由后端返回对应提示，两端一致。
 */
@Api(tags = "统一认证")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @ApiOperation("登录（账号+密码，两端共用）")
    @PostMapping("/login")
    public Result<LoginResponse> login(@Validated @RequestBody LoginRequest req) {
        return Result.success(userService.login(req));
    }

    @ApiOperation("微信登录（批次I：wx.login code → openid → JWT）")
    @PostMapping("/wx-login")
    public Result<LoginResponse> wxLogin(@Validated @RequestBody WxLoginRequest req) {
        return Result.success(userService.wxLogin(req.getCode()));
    }

    @ApiOperation("当前登录人信息")
    @GetMapping("/me")
    public Result<UserVO> me() {
        return Result.success(userService.getById(AuthContext.userId()));
    }
}