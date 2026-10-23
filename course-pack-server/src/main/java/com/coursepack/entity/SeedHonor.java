package com.coursepack.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;

/**
 * 种子教师荣誉认证（案例数走聚合）
 */
@Data
@TableName("seed_honors")
public class SeedHonor {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private LocalDate certifiedAt;
}
