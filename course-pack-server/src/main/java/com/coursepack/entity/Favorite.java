package com.coursepack.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 课程收藏（教师↔课程，toggle 语义），对应表 favorites
 */
@Data
@TableName("favorites")
public class Favorite {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 教师 users.id */
    private Long userId;

    /** 课程 courses.id */
    private Long courseId;

    private LocalDateTime createdAt;
}
