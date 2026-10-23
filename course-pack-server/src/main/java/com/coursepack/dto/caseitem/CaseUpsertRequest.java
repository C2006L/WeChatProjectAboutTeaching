package com.coursepack.dto.caseitem;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 教学案例新增/更新请求（客户端提交页）
 */
@Data
@ApiModel("教学案例提交")
public class CaseUpsertRequest {

    @ApiModelProperty("标题")
    @NotBlank(message = "案例标题不能为空")
    private String title;

    @ApiModelProperty("学段 小学1-3年级|小学4-6年级")
    @NotBlank(message = "学段不能为空")
    private String grade;

    @ApiModelProperty("主题（中文标签）")
    @NotBlank(message = "主题不能为空")
    private String theme;

    @ApiModelProperty("关联课程")
    private String courseTitle;

    @ApiModelProperty("摘要")
    private String summary;

    @ApiModelProperty("案例正文")
    private String content;

    @ApiModelProperty("教学反思")
    private String reflection;

    @ApiModelProperty("提交动作 DRAFT | PENDING（缺省 PENDING）")
    private String action;
}
