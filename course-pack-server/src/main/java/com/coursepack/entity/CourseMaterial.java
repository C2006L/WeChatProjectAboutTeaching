package com.coursepack.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 课程材料（教案/课件/教具/视频），对应表 course_materials
 */
@Data
@TableName("course_materials")
public class CourseMaterial {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联课程 courses.id */
    private Long courseId;

    /** plan|slides|toolkit|video */
    private String type;

    /** 材料名 */
    private String name;

    /** 下载地址（未上传时为空） */
    private String url;

    /** 展示用大小 */
    private String size;

    private LocalDateTime createdAt;

    @TableLogic
    private Integer deleted;
}
