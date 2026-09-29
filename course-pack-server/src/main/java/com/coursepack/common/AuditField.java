package com.coursepack.common;

/**
 * 课程审核领域（仅审核员 user.audit_field 使用，用于过滤待审队列）
 *   EDU 教育学审核  SOC 社会学审核  PSY 儿童心理学审核
 * 注：审核中心四环节 = 种子(教育学) → 发芽(儿童心理学) → 长叶(社会学) → 小树(负责人终审，仅 ADMIN)
 */
public enum AuditField {
    EDU, PSY, SOC
}