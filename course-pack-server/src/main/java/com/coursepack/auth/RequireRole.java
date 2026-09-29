package com.coursepack.auth;

import com.coursepack.common.UserRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 后端角色校验注解：声明允许访问的角色的 Controller 方法上。
 * 前端隐藏不算锁，后端拒绝才是真锁（双层校验的"后层"）。
 *
 * 示例：@RequireRole(UserRole.TEACHER)
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {

    UserRole[] value();
}