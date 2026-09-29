package com.coursepack.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 备课草稿（支撑"电脑备课、手机续写"：两端写同一张表），对应表 lesson_draft
 */
@Data
@TableName("lesson_draft")
public class LessonDraft {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 归属教师 users.id */
    private Long userId;

    /** 关联课程（可选） */
    private Long courseId;

    private String title;

    /** 左侧课程大纲（HTML/JSON 字符串） */
    private String outline;

    /** 右侧编辑器正文（HTML 富文本） */
    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}