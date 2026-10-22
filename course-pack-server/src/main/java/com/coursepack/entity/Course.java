package com.coursepack.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 生命教育课程（批次H），对应表 courses
 * 数组字段（目标/价值/流程/敏感问题）以 JSON 文本存储，Service 层负责与 List<String>/List<CourseStep> 互转。
 */
@Data
@TableName("courses")
public class Course {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 课程标题 */
    private String title;

    /** 主题 body|family|respect|growth|general */
    private String theme;

    /** 学段 小学1-3年级|小学4-6年级 */
    private String grade;

    /** 时长（分钟） */
    private Integer duration;

    /** 版本 精简版|标准版 */
    private String version;

    /** 一句话简介 */
    private String summary;

    /** 教学目标 JSON string[] */
    private String objectivesJson;

    /** 价值总结 JSON string[] */
    private String valuesJson;

    /** 教学流程 JSON [{index,title,desc,duration}] */
    private String stepsJson;

    /** 敏感问题ID JSON number[] */
    private String sensitiveQaJson;

    /** 封面色（可空，前端按主题兜底） */
    private String cover;

    /** 0 下架 | 1 上架 */
    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
