package com.coursepack.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 队伍成员（user 关联行实时聚合必修进度；种子演示行用 seedDone）
 */
@Data
@TableName("team_members")
public class TeamMember {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long teamId;

    private Long userId;

    private String name;

    /** 队长 | 成员 */
    private String role;

    private Integer sort;

    /** 种子演示用户的必修完成数（userId 非空时忽略） */
    private Integer seedDone;
}
