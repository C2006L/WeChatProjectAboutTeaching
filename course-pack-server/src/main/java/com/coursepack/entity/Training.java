package com.coursepack.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 培训课程（批次J：必修 5 + 选修 3，与前端 mock 同构）
 */
@Data
@TableName("trainings")
public class Training {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    /** required 必修 / elective 选修 */
    private String kind;

    private Integer duration;

    private String introduction;

    private String videoUrl;

    private String outlineJson;

    private String materialsJson;

    private Integer sort;
}
