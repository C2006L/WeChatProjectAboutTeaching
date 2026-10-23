package com.coursepack.dto.admin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 资源库条目（对齐管理端 ResourceItem）
 */
@Data
@ApiModel("资源条目")
public class ResourceVO {

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("资源名")
    private String title;

    @ApiModelProperty("类型 教案模板|PPT模板|评估量表|绘本清单")
    private String kind;

    @ApiModelProperty("下载次数")
    private Integer downloads;

    @ApiModelProperty("展示大小")
    private String size;

    @ApiModelProperty("更新日期 yyyy-MM-dd")
    private String updatedAt;
}
