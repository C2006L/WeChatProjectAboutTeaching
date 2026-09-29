package com.coursepack.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一响应体，所有 Controller 返回值都包装为此类
 *
 * 【学习要点】
 *
 * 1. 为什么用泛型 <T>？
 *    - 不同接口返回不同类型数据（UserVO、CourseVO 等）
 *    - 泛型在编译期检查类型安全，避免运行时 ClassCastException
 *    - 例如：Result<UserVO> 只能放 UserVO，放 CourseVO 会编译报错
 *
 * 2. 为什么用静态工厂方法（success/fail）？
 *    - 比直接 new Result(0, "success", data) 更简洁
 *    - 方法名即语义，代码可读性好
 *    - 可以缓存常用对象（本例未做，但可以扩展）
 *
 * 3. Lombok 注解说明：
 *    - @Data：自动生成 getter/setter/toString/equals/hashCode
 *    - @NoArgsConstructor：生成无参构造器
 *    - @AllArgsConstructor：生成全参构造器
 *
 * 【面试可能问】
 * Q: Result<UserVO> 和 Result<CourseVO> 是同一个类型吗？
 * A: 不是。Java 泛型不支持协变，Result<UserVO> 和 Result<CourseVO>
 *    是不同的类型，不能互相赋值。这是编译期类型安全的保证。
 *
 * @param <T> 业务数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    /**
     * 状态码：0=成功，其他=失败
     * 用 Integer 而非 int，因为序列化为 JSON 时 null 和 0 有区别
     */
    private Integer code;

    /**
     * 提示信息，前端直接展示给用户
     */
    private String message;

    /**
     * 业务数据，泛型类型
     */
    private T data;

    // ==================== 静态工厂方法 ====================

    /**
     * 成功响应（无数据）
     * 使用场景：POST/PUT/DELETE 操作成功，不需要返回数据
     *
     * 示例：return Result.success();
     */
    public static <T> Result<T> success() {
        return new Result<>(0, "success", null);
    }

    /**
     * 成功响应（有数据）
     * 使用场景：GET 查询成功，返回查询结果
     *
     * 示例：return Result.success(userVO);
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(0, "success", data);
    }

    /**
     * 失败响应（使用错误码枚举）
     * 使用场景：业务逻辑校验失败
     *
     * 示例：return Result.fail(ErrorCode.NOT_LOGIN);
     */
    public static <T> Result<T> fail(ErrorCode errorCode) {
        return new Result<>(errorCode.getCode(), errorCode.getMessage(), null);
    }

    /**
     * 失败响应（自定义消息）
     * 使用场景：参数校验失败，需要返回具体的校验错误信息
     *
     * 示例：return Result.fail(10001, "用户名不能为空");
     */
    public static <T> Result<T> fail(Integer code, String message) {
        return new Result<>(code, message, null);
    }

}
