package com.coursepack.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 教师培训学习进度（每教师每课程一行）
 */
@Data
@TableName("teacher_training")
public class TeacherTraining {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long trainingId;

    /** 0-100 */
    private Integer progress;

    private LocalDateTime updatedAt;
}
