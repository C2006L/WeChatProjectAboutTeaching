package com.coursepack.common;

/**
 * 角色（最终版，不可增删）
 *   TEACHER       教师（小程序 + Web 教师工作台）
 *   AUDITOR       课程审核员（仅 Web，仅审核中心）
 *   ADMIN         项目管理员（仅 Web，全模块 + 终审）
 * 注意：SEED_TEACHER 不是独立角色，= TEACHER 且 users.is_seed=1
 */
public enum UserRole {
    TEACHER, AUDITOR, ADMIN
}