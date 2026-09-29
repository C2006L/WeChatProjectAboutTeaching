package com.coursepack.controller.common;

import com.coursepack.common.BusinessException;
import com.coursepack.common.ErrorCode;
import com.coursepack.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试接口
 * 用于验证 Phase 1 基础设施是否正常工作
 *
 * 验证步骤：
 *   1. 启动项目
 *   2. 访问 http://localhost:8080/doc.html 查看 API 文档
 *   3. 调用 /api/public/test/success → 验证统一响应体
 *   4. 调用 /api/public/test/error   → 验证全局异常处理
 */
@Api(tags = "测试接口")
@RestController
@RequestMapping("/api/public/test")
public class TestController {

    /**
     * 测试成功响应
     * 预期返回：{"code": 0, "message": "success", "data": "Phase 1 基础设施正常"}
     */
    @ApiOperation("测试成功响应")
    @GetMapping("/success")
    public Result<String> testSuccess() {
        return Result.success("Phase 1 基础设施正常");
    }

    /**
     * 测试业务异常
     * 预期返回：{"code": 10003, "message": "请先登录", "data": null}
     */
    @ApiOperation("测试业务异常")
    @GetMapping("/error")
    public Result<Void> testError() {
        throw new BusinessException(ErrorCode.NOT_LOGIN);
    }

    /**
     * 测试系统异常
     * 预期返回：{"code": 90001, "message": "系统繁忙，请稍后重试", "data": null}
     */
    @ApiOperation("测试系统异常")
    @GetMapping("/system-error")
    public Result<Void> testSystemError() {
        // 模拟空指针异常
        String str = null;
        str.length();
        return Result.success();
    }

}
