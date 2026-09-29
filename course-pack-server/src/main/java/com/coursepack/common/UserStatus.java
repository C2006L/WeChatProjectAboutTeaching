package com.coursepack.common;

/**
 * 账号状态（客户端与 Web 端共用同一状态机，行为必须一致）
 *   PENDING   待审核 → 两端均"账号审核中"锁定
 *   ACTIVE    正常通过
 *   REJECTED  驳回 → 返回原因 + 重新提交资料
 *   DISABLED  停用 → 登录直接拦截
 */
public enum UserStatus {
    PENDING, ACTIVE, REJECTED, DISABLED
}