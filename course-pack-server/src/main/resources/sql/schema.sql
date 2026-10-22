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
  `openid`      VARCHAR(64)  NULL COMMENT '微信 openid（批次I；wx 登录绑定标识）',
  `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除 0正常|1删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_openid` (`openid`)
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

-- 生命教育课程（批次H：课程域主表；管理端 CRUD + 小程序端浏览共用）
CREATE TABLE IF NOT EXISTS `courses` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title`         VARCHAR(128) NOT NULL COMMENT '课程标题',
  `theme`         VARCHAR(20)  NOT NULL COMMENT '主题 body|family|respect|growth|general',
  `grade`         VARCHAR(20)  NOT NULL COMMENT '学段 小学1-3年级|小学4-6年级',
  `duration`      INT          NOT NULL COMMENT '时长（分钟）',
  `version`       VARCHAR(10)  NOT NULL COMMENT '版本 精简版|标准版',
  `summary`       VARCHAR(255) NOT NULL COMMENT '一句话简介',
  `objectives_json` TEXT     NULL COMMENT '教学目标 JSON string[]',
  `values_json`     TEXT     NULL COMMENT '价值总结 JSON string[]',
  `steps_json`      TEXT     NULL COMMENT '教学流程 JSON [{index,title,desc,duration}]',
  `sensitive_qa_json` TEXT   NULL COMMENT '敏感问题ID JSON number[]',
  `cover`         VARCHAR(20)  NULL COMMENT '封面色（可空，前端按主题兜底）',
  `status`        TINYINT      NOT NULL DEFAULT 1 COMMENT '0 下架 | 1 上架',
  `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted`       TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_theme_grade` (`theme`, `grade`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='生命教育课程';

-- 课程材料（教案/课件/教具/视频；数量统计与下载列表的数据源）
CREATE TABLE IF NOT EXISTS `course_materials` (
  `id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `course_id`  BIGINT       NOT NULL COMMENT '关联课程 courses.id',
  `type`       VARCHAR(10)  NOT NULL COMMENT 'plan|slides|toolkit|video',
  `name`       VARCHAR(128) NOT NULL COMMENT '材料名',
  `url`        VARCHAR(512) NULL COMMENT '下载地址（未上传时为空）',
  `size`       VARCHAR(20)  NULL COMMENT '展示用大小',
  `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted`    TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_course` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程材料';

-- 课程收藏（教师收藏课程；toggle 语义）
CREATE TABLE IF NOT EXISTS `favorites` (
  `id`         BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id`    BIGINT   NOT NULL COMMENT '教师 users.id',
  `course_id`  BIGINT   NOT NULL COMMENT '课程 courses.id',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_course` (`user_id`, `course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程收藏';