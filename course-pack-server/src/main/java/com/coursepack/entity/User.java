package com.coursepack.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 统一账号表（教师/审核员/管理员共用一套），对应表 users
 */
@Data
@TableName("users")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 登录账号（两端共用同一凭证） */
    private String username;

    /** bcrypt 摘要 */
    private String password;

    /** 姓名 */
    private String name;

    /** 角色：TEACHER | AUDITOR | ADMIN */
    private String role;

    /** 状态：PENDING | ACTIVE | REJECTED | DISABLED */
    private String status;

    /** 是否种子教师 0/1（种子教师 = TEACHER + is_seed=1） */
    private Integer isSeed;

    /** 审核领域 EDU | PSY | SOC（仅审核员） */
    private String auditField;

    /** 驳回原因 */
    private String rejectReason;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}