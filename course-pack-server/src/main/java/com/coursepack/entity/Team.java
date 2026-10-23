package com.coursepack.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 支教队伍（批次J4）
 */
@Data
@TableName("teams")
public class Team {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String org;

    private String area;

    private Integer usageCount;
}
