package com.coursepack.dto.course;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 材料数量统计（对齐接口规范 CourseSummary.materials）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("材料数量")
public class MaterialsVO {

    @ApiModelProperty("教案份数")
    private Integer plan;

    @ApiModelProperty("课件份数")
    private Integer slides;

    @ApiModelProperty("教具份数")
    private Integer toolkit;

    @ApiModelProperty("视频份数")
    private Integer video;
}
