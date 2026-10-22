package com.coursepack.dto.course;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 教学流程步骤（steps_json 元素结构，对齐接口规范 CourseDetail.steps）
 */
@Data
@ApiModel("教学流程步骤")
public class CourseStep {

    @ApiModelProperty("序号，1 起")
    private Integer index;

    @ApiModelProperty("环节名，如 热身活动")
    private String title;

    @ApiModelProperty("环节说明/任务")
    private String desc;

    @ApiModelProperty("时长（分钟）")
    private Integer duration;
}
