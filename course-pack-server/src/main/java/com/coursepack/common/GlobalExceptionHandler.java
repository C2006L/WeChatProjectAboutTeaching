package com.coursepack.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 *
 * 【学习要点】
 *
 * 1. @RestControllerAdvice 是什么？
 *    - = @ControllerAdvice + @ResponseBody
 *    - @ControllerAdvice：全局拦截所有 Controller 抛出的异常（基于 AOP）
 *    - @ResponseBody：返回值自动序列化为 JSON
 *    - 效果：Controller 里不用写 try-catch，异常自动被这里捕获
 *
 * 2. @ExceptionHandler 的匹配规则？
 *    - 优先匹配最具体的异常类型
 *    - BusinessException extends RuntimeException extends Exception
 *    - 如果抛出 BusinessException，会优先进入 handleBusinessException，而非 handleException
 *
 * 3. 为什么需要兜底的 handleException(Exception e)？
 *    - 防止未预期的异常直接返回 500 堆栈信息给前端
 *    - 统一返回 {"code": 90001, "message": "系统繁忙"} 给前端
 *    - 完整堆栈只记录在服务端日志中，不泄露给用户
 *
 * 【面试可能问】
 * Q: @RestControllerAdvice 和 @ControllerAdvice 的区别？
 * A: @RestControllerAdvice 多了 @ResponseBody，返回值直接序列化为 JSON。
 *    @ControllerAdvice 需要配合 @ResponseBody 使用才能返回 JSON。
 *
 * Q: 什么是 AOP？在项目中如何应用的？
 * A: AOP（面向切面编程）是在不修改业务代码的情况下，统一添加横切逻辑。
 *    全局异常处理就是 AOP 的典型应用：
 *    - 切点：所有 Controller 方法
 *    - 通知：异常处理后返回 Result
 *    - 好处：Controller 只关注业务逻辑，异常处理集中管理
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * 使用场景：Service 层主动抛出的 BusinessException
     *
     * 示例：Service 中 throw new BusinessException(ErrorCode.NOT_LOGIN)
     * 这里捕获后返回：{"code": 10003, "message": "请先登录", "data": null}
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, message={}", e.getErrorCode().getCode(), e.getMessage());
        return Result.fail(e.getErrorCode());
    }

    /**
     * 捕获参数校验异常（@RequestBody + @Valid 场景）
     * 使用场景：Controller 方法参数标注了 @Valid，DTO 字段标注了 @NotBlank 等
     *
     * 示例：DTO 中 @NotBlank(message = "用户名不能为空") private String username;
     * 前端传了空用户名 → 自动校验失败 → 这里捕获
     * 返回：{"code": 10001, "message": "用户名不能为空", "data": null}
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidException(MethodArgumentNotValidException e) {
        // 收集所有字段校验错误信息，用逗号拼接
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数校验失败: {}", message);
        return Result.fail(ErrorCode.PARAM_INVALID.getCode(), message);
    }

    /**
     * 捕获参数绑定异常（表单提交场景）
     */
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数绑定失败: {}", message);
        return Result.fail(ErrorCode.PARAM_INVALID.getCode(), message);
    }

    /**
     * 兜底异常处理
     * 使用场景：所有未被上面捕获的异常（如 NullPointerException、数据库异常等）
     *
     * 注意：生产环境不能把堆栈信息返回给前端，只返回友好提示
     * 完整错误信息记录在日志中，便于排查
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.fail(ErrorCode.SYSTEM_ERROR);
    }

}
