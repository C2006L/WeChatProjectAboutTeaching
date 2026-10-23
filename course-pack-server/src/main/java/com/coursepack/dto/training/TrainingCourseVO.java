package com.coursepack.dto.training;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 管理端培训课程行（admin TrainingCourseRow 对齐）
 */
@Data
@ApiModel("培训管理课程行")
public class TrainingCourseVO {

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("必修/选修")
    private String type;

    @ApiModelProperty("时长（分钟）")
    private Integer duration;

    @ApiModelProperty("已完成教师数")
    private Integer completedCount;

    @ApiModelProperty("应完成教师数（required=教师总数）")
    private Integer assignedCount;
}
