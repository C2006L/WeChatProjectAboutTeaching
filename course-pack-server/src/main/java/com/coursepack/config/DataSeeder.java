package com.coursepack.config;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.coursepack.entity.CourseAudit;
import com.coursepack.entity.User;
import com.coursepack.mapper.CourseAuditMapper;
import com.coursepack.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 演示账号种子（幂等：仅缺账号时插入）。密码统一 123456（bcrypt 存储）。
 * 账号表见 README。需先执行 resources/sql/schema.sql 建表。
 * 另承担轻量 DDL 迁移（users.openid 列，幂等：重复执行忽略 Duplicate column）。
 */
@Slf4j
@Component
@Order(100)
public class DataSeeder implements CommandLineRunner {

    private final UserMapper userMapper;
    private final CourseAuditMapper courseAuditMapper;
    private final JdbcTemplate jdbcTemplate;

    public DataSeeder(UserMapper userMapper, CourseAuditMapper courseAuditMapper, JdbcTemplate jdbcTemplate) {
        this.userMapper = userMapper;
        this.courseAuditMapper = courseAuditMapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        patchSchema();
        String hash = BCrypt.hashpw("123456", BCrypt.gensalt());
        List<User> seed = Arrays.asList(
                user("teacher01", "李老师", "TEACHER", "ACTIVE", 0, null, null),
                user("teacher02", "王老师", "TEACHER", "PENDING", 0, null, null),
                user("teacher03", "赵老师", "TEACHER", "REJECTED", 0, null, "支教证明材料不清晰"),
                user("teacher04", "张老师", "TEACHER", "ACTIVE", 1, null, null),
                user("auditor01", "陈教授", "AUDITOR", "ACTIVE", 0, "PSY", null),
                user("auditor02", "周教授", "AUDITOR", "ACTIVE", 0, "EDU", null),
                user("admin01", "项目管理员", "ADMIN", "ACTIVE", 0, null, null)
        );
        for (User u : seed) {
            Long exists = userMapper.selectCount(
                    Wrappers.<User>lambdaQuery().eq(User::getUsername, u.getUsername()));
            if (exists != null && exists > 0) {
                continue;
            }
            u.setPassword(hash);
            userMapper.insert(u);
            log.info("[seed] 创建演示账号 {}", u.getUsername());
        }

        // 课程审核样例（幂等：按 course_id + title 判重）。demo01/demo02/demo03/audit_pending 各领域。
        Long anyAudit = courseAuditMapper.selectCount(Wrappers.emptyWrapper());
        if (anyAudit == null || anyAudit == 0) {
            List<CourseAudit> audits = Arrays.asList(
                    audit(11L, "我的身体，我做主", "PSY", "李老师", 35),
                    audit(12L, "安全第一课", "EDU", "张老师", 60),
                    audit(13L, "学会说“不”", "SOC", "王老师", 20)
            );
            for (CourseAudit a : audits) {
                courseAuditMapper.insert(a);
            }
            log.info("[seed] 创建课程审核样例 {} 条", audits.size());
        }
    }

    /** 轻量 DDL 迁移：老库补列（MySQL 不支持 ADD COLUMN IF NOT EXISTS，按报错信息幂等跳过） */
    private void patchSchema() {
        try {
            jdbcTemplate.execute("ALTER TABLE `users` ADD COLUMN `openid` VARCHAR(64) NULL COMMENT '微信 openid' AFTER `reject_reason`");
            jdbcTemplate.execute("ALTER TABLE `users` ADD INDEX `idx_openid` (`openid`)");
            log.info("[seed] 迁移：users 增补 openid 列完成");
        } catch (Exception e) {
            if (String.valueOf(e.getMessage()).contains("Duplicate column name 'openid'")) {
                log.debug("[seed] users.openid 列已存在，跳过迁移");
            } else {
                throw e;
            }
        }
        try {
            jdbcTemplate.execute("ALTER TABLE `team_members` ADD COLUMN `seed_done` INT NULL COMMENT '种子演示必修完成数（userId 非空时忽略）'");
            log.info("[seed] 迁移：team_members 增补 seed_done 列完成");
        } catch (Exception e) {
            if (String.valueOf(e.getMessage()).contains("Duplicate column name 'seed_done'")) {
                log.debug("[seed] team_members.seed_done 列已存在，跳过迁移");
            } else {
                throw e;
            }
        }
    }

    private CourseAudit audit(Long courseId, String title, String field, String applicant, int progress) {
        CourseAudit a = new CourseAudit();
        a.setCourseId(courseId);
        a.setTitle(title);
        a.setAuditField(field);
        a.setApplicant(applicant);
        a.setSubmitTime(LocalDateTime.now().minusDays(1));
        a.setStatus("PENDING");
        a.setProgress(progress);
        return a;
    }

    private User user(String username, String name, String role, String status,
                      int isSeed, String auditField, String reason) {
        User u = new User();
        u.setUsername(username);
        u.setName(name);
        u.setRole(role);
        u.setStatus(status);
        u.setIsSeed(isSeed);
        u.setAuditField(auditField);
        u.setRejectReason(reason);
        return u;
    }
}