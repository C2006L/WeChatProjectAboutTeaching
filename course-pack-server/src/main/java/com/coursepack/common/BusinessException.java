package com.coursepack.common;

import lombok.Getter;

/**
 * 业务异常
 *
 * 【学习要点】
 *
 * 1. 为什么继承 RuntimeException 而不是 Exception？
 *    - RuntimeException 是"非受检异常"（unchecked exception）
 *    - 不需要在方法签名上声明 throws，也不强制 try-catch
 *    - Spring 的 @Transactional 默认只回滚 RuntimeException
 *    - 业务异常应该"向上传播"直到被 GlobalExceptionHandler 捕获
 *
 * 2. 为什么不用 IllegalArgumentException？
 *    - IllegalArgumentException 是 JDK 自带的，没有业务语义
 *    - 自定义异常可以携带 ErrorCode，便于统一处理
 *
 * 3. 为什么用@Getter 而不是 @Data？
 *    - 异常类不需要 setter（异常创建后不应修改）
 *    - 不需要 toString/equals/hashCode（异常类不作为 Map key）
 *
 * 【面试可能问】
 * Q: RuntimeException 和 Exception 的区别？
 * A: Exception 是受检异常，必须 try-catch 或 throws 声明；
 *    RuntimeException 是非受检异常，不强制处理。
 *    NullPointerException、IllegalArgumentException 都是 RuntimeException。
 *
 * Q: Spring 事务在什么情况下会回滚？
 * A: 默认只回滚 RuntimeException 和 Error。如果抛出 checked Exception，
 *    事务不会回滚（除非配置 rollbackFor = Exception.class）。
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 错误码枚举
     */
    private final ErrorCode errorCode;

    /**
     * 使用错误码构造业务异常
     *
     * 示例：throw new BusinessException(ErrorCode.NOT_LOGIN);
     *
     * @param errorCode 错误码枚举
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    /**
     * 使用错误码 + 自定义消息构造
     *
     * 示例：throw new BusinessException(ErrorCode.PARAM_INVALID, "用户名不能为空");
     *
     * @param errorCode    错误码枚举
     * @param customMessage 自定义错误消息（覆盖枚举中的默认消息）
     */
    public BusinessException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
    }

}
