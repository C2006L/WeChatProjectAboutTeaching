package com.coursepack.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 统一错误码定义
 *
 * 【学习要点】
 *
 * 1. 为什么用枚举而不用常量类？
 *    - 枚举有命名空间（ErrorCode.PARAM_INVALID），不会冲突
 *    - 枚举可以携带多个属性（code + message）
 *    - IDE 自动补全，写错会编译报错
 *    - 常量类只有值，没有语义信息
 *
 * 2. 错误码分区规则：
 *    10000-10099  通用错误
 *    20000-20099  用户/登录
 *    30000-30099  课程包
 *    40000-40099  视频
 *    50000-50099  AI 对话
 *    60000-60099  社区/案例
 *    90000-90099  系统错误
 *
 * 3. Lombok 注解说明：
 *    - @Getter：生成 getter 方法
 *    - @AllArgsConstructor：生成全参构造器（枚举的构造器只能是 private）
 *
 * 【面试可能问】
 * Q: 为什么错误码用 Integer 而不用 String？
 * A: Integer 比较速度快，前端 switch/case 更方便，占用空间小。
 *
 * Q: 枚举的构造器为什么是 private？
 * A: 枚举的实例数量是固定的（在枚举类中定义的那些），外部不能 new 新实例。
 *    这正是"单例模式"的延伸——枚举是线程安全的单例。
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    // ===== 通用错误 10000-10099 =====
    SUCCESS(0, "成功"),
    PARAM_INVALID(10001, "参数校验失败"),
    RESOURCE_NOT_FOUND(10002, "资源不存在"),
    NOT_LOGIN(10003, "请先登录"),
    NO_PERMISSION(10004, "无操作权限"),

    // ===== 用户/登录错误 20000-20099 =====
    WX_CODE_INVALID(20001, "微信授权码无效"),
    WX_LOGIN_FAILED(20002, "微信登录失败"),
    TOKEN_EXPIRED(20003, "登录已过期"),
    REFRESH_TOKEN_INVALID(20004, "请重新登录"),
    USER_DISABLED(20005, "账号已被禁用"),
    USER_PENDING(20006, "账号审核中，请耐心等待"),
    USER_REJECTED(20007, "账号审核未通过"),

    // ===== 课程包错误 30000-30099 =====
    COURSE_NOT_FOUND(30001, "课程包不存在"),
    COURSE_OFFLINE(30002, "课程包已下架"),
    COURSE_RESOURCE_NOT_FOUND(30003, "课程资源不存在"),

    // ===== 视频错误 40000-40099 =====
    VIDEO_NOT_FOUND(40001, "视频不存在"),
    VIDEO_OFFLINE(40002, "视频未上架"),

    // ===== AI 对话错误 50000-50099 =====
    AI_RATE_LIMIT(50001, "提问太频繁，请稍后再试"),
    AI_CALL_FAILED(50002, "AI 服务开小差了"),
    AI_CONTEXT_TOO_LONG(50003, "对话上下文过长，请开启新对话"),

    // ===== 社区/案例错误 60000-60099 =====
    POST_NOT_FOUND(60001, "帖子不存在"),
    NO_POST_PERMISSION(60002, "无发帖权限"),
    CASE_NOT_FOUND(60003, "案例不存在"),

    // ===== 系统错误 90000-90099 =====
    SYSTEM_ERROR(90001, "系统繁忙，请稍后重试"),
    OSS_UPLOAD_FAILED(90002, "文件上传失败"),
    OSS_DOWNLOAD_FAILED(90003, "文件下载失败"),
    REDIS_CONNECTION_FAILED(90004, "缓存服务异常");

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误消息（用户可读）
     */
    private final String message;

}
