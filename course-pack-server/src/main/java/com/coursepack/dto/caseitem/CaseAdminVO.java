package com.coursepack.dto.caseitem;

import com.coursepack.dto.dashboard.AuditCommentVO;
import com.coursepack.dto.dashboard.AuditStageVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 管理端案例条目（对齐 CaseItem 类型：四环节步骤 + 意见记录）
 */
@Data
@ApiModel("管理端教学案例")
public class CaseAdminVO {

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("上传人")
    private String uploader;

    @ApiModelProperty("队伍（批次J4 前未分配）")
    private String team;

    @ApiModelProperty("上传日期 yyyy-MM-dd")
    private String uploadedAt;

    @ApiModelProperty("APPROVED | PENDING")
    private String status;

    @ApiModelProperty("精选")
    private Boolean featured;

    @ApiModelProperty("摘要")
    private String summary;

    @ApiModelProperty("四环节流转(完成态)")
    private List<AuditStageVO> stages;

    @ApiModelProperty("意见记录")
    private List<AuditCommentVO> comments;
}
