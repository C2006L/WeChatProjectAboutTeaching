-- ============================================================
-- 伴芽 · 统一认证与角色权限体系  DDL（批次E）
-- 在 MySQL course_pack 库执行。幂等：CREATE TABLE IF NOT EXISTS
-- ============================================================

-- 统一账号表（教师/审核员/管理员共用一套）
CREATE TABLE IF NOT EXISTS `users` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username`    VARCHAR(64)  NOT NULL COMMENT '登录账号（两端共用同一凭证）',
  `password`    VARCHAR(100) NOT NULL COMMENT 'bcrypt 摘要',
  `name`        VARCHAR(64)  NOT NULL COMMENT '姓名',
  `role`        VARCHAR(20)  NOT NULL COMMENT 'TEACHER|AUDITOR|ADMIN',
  `status`      VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING|ACTIVE|REJECTED|DISABLED',
  `is_seed`     TINYINT      NOT NULL DEFAULT 0 COMMENT '是否种子教师 0/1（种子教师=TEACHER+is_seed=1）',
  `audit_field` VARCHAR(10)  NULL COMMENT '审核领域 EDU|PSY|SOC（仅审核员）',
  `reject_reason` VARCHAR(255) NULL COMMENT '驳回原因',
  `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除 0正常|1删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='统一账号表';

-- 备课草稿（支撑"电脑备课、手机续写"：两端写同一张表）
CREATE TABLE IF NOT EXISTS `lesson_draft` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id`     BIGINT       NOT NULL COMMENT '归属教师 users.id',
  `course_id`   BIGINT       NULL COMMENT '关联课程（可选）',
  `title`       VARCHAR(128) NULL COMMENT '备课标题',
  `outline`     TEXT         NULL COMMENT '左侧课程大纲',
  `content`     MEDIUMTEXT   NULL COMMENT '右侧编辑器正文',
  `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted`     TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='备课草稿';

-- 最新动态时间线（审核/教师状态操作写入；看板"最新动态"数据源）
CREATE TABLE IF NOT EXISTS `timeline_log` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `action`      VARCHAR(64)  NOT NULL COMMENT '操作类型（如 TEACHER_APPROVED / AUDIT_PASSED）',
  `content`     VARCHAR(255) NOT NULL COMMENT '动态文案',
  `operator_id` BIGINT       NULL COMMENT '操作人 users.id（可为空）',
  `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted`     TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_created` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='最新动态时间线';

-- 课程审核（AUDITOR 专用；按 audit_field 过滤待审队列）
CREATE TABLE IF NOT EXISTS `course_audit` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `course_id`     BIGINT       NOT NULL COMMENT '关联课程ID',
  `title`         VARCHAR(128) NOT NULL COMMENT '课程标题',
  `audit_field`   VARCHAR(10)  NOT NULL COMMENT '审核领域 EDU|PSY|SOC',
  `applicant`     VARCHAR(64)  NULL COMMENT '申请人（教师）',
  `submit_time`   DATETIME     NULL COMMENT '提交时间',
  `auditor_id`    BIGINT       NULL COMMENT '审核员 users.id（处理后回填）',
  `status`        VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING|APPROVED|REJECTED',
  `progress`      TINYINT      NOT NULL DEFAULT 0 COMMENT '拟物进度 0-100',
  `reject_reason` VARCHAR(255) NULL COMMENT '驳回原因',
  `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted`       TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_status_field` (`status`, `audit_field`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程审核';