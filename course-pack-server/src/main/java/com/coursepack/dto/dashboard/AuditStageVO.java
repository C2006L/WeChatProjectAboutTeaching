package com.coursepack.dto.dashboard;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 审核环节（课程与案例共用 VO，对齐管理端 AuditStage）
 */
@Data
@ApiModel("审核环节")
public class AuditStageVO {

    @ApiModelProperty("education|psychology|sociology|principal")
    private String key;

    @ApiModelProperty("环节中文名")
    private String label;

    @ApiModelProperty("是否完成")
    private Boolean done;
}
