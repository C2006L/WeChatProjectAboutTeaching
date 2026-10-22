package com.coursepack.dto.course;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 管理端课程新增/更新请求（字段与 CourseDetail 管理项对齐；材料以数量形式提交，后端落脚为 course_materials 行）
 */
@Data
@ApiModel("课程新增/更新请求")
public class CourseUpsertRequest {

    @ApiModelProperty("标题")
    @NotBlank(message = "课程标题不能为空")
    private String title;

    @ApiModelProperty("主题 body|family|respect|growth|general")
    @NotBlank(message = "主题不能为空")
    private String theme;

    @ApiModelProperty("学段 小学1-3年级|小学4-6年级")
    @NotBlank(message = "学段不能为空")
    private String grade;

    @ApiModelProperty("时长（分钟）")
    @NotNull(message = "时长不能为空")
    @Min(value = 5, message = "时长至少 5 分钟")
    private Integer duration;

    @ApiModelProperty("版本 精简版|标准版")
    @NotBlank(message = "版本不能为空")
    private String version;

    @ApiModelProperty("一句话简介")
    @NotBlank(message = "简介不能为空")
    private String summary;

    @ApiModelProperty("封面色（可空）")
    private String cover;

    @ApiModelProperty("0 下架 | 1 上架，缺省按 1（上架）")
    private Integer status;

    @ApiModelProperty("教学目标")
    private List<String> objectives;

    @ApiModelProperty("价值总结")
    private List<String> values;

    @ApiModelProperty("教学流程")
    private List<CourseStep> steps;

    @ApiModelProperty("关联敏感问题ID")
    private List<Long> sensitiveQa;

    @ApiModelProperty("材料数量（用于生成材料行）")
    private MaterialsVO materials;
}
