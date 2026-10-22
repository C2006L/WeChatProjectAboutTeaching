package com.coursepack.dto.course;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 课程视图（对齐接口规范 CourseSummary CourseDetail 的并集；管理端多带 status/updatedAt）
 * 聚合字段 materials 为材料数量统计 {plan,slides,toolkit,video}。
 */
@Data
@ApiModel("课程")
public class CourseVO {

    @ApiModelProperty("课程ID")
    private Long id;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("主题编码 body|family|respect|growth|general")
    private String theme;

    @ApiModelProperty("主题中文名，如 身体与安全（后端映射）")
    private String themeLabel;

    @ApiModelProperty("学段 小学1-3年级|小学4-6年级")
    private String grade;

    @ApiModelProperty("时长（分钟）")
    private Integer duration;

    @ApiModelProperty("版本 精简版|标准版")
    private String version;

    @ApiModelProperty("一句话简介")
    private String summary;

    @ApiModelProperty("封面色（可空，前端按主题兜底）")
    private String cover;

    @ApiModelProperty("材料数量 {plan,slides,toolkit,video}")
    private MaterialsVO materials;

    @ApiModelProperty("教学目标")
    private List<String> objectives;

    @ApiModelProperty("价值总结")
    private List<String> values;

    @ApiModelProperty("关联敏感问题ID")
    private List<Long> sensitiveQa;

    @ApiModelProperty("教学流程")
    private List<CourseStep> steps;

    /** 管理端字段 */
    @ApiModelProperty("0 下架 | 1 上架（管理端）")
    private Integer status;

    @ApiModelProperty("更新时间 yyyy-MM-dd HH:mm:ss（管理端）")
    private LocalDateTime updatedAt;
}
