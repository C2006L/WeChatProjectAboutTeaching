package com.coursepack.dto.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户信息（登录后返回；前端据此做角色/状态分流）
 */
@Data
@ApiModel("用户信息")
public class UserVO {

    @ApiModelProperty("用户ID")
    private Long id;

    @ApiModelProperty("登录账号")
    private String username;

    @ApiModelProperty("姓名")
    private String name;

    /** TEACHER | AUDITOR | ADMIN */
    private String role;

    /** PENDING | ACTIVE | REJECTED | DISABLED */
    private String status;

    /** 是否种子教师 0/1 */
    private Integer isSeed;

    /** 审核领域 EDU | PSY | SOC（仅审核员） */
    private String auditField;

    /** 驳回原因（仅 REJECTED 时非空） */
    private String rejectReason;
}