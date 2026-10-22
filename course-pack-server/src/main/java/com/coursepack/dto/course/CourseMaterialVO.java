package com.coursepack.dto.course;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 课程材料列表项（下载列表；对齐接口规范 Material）
 */
@Data
@ApiModel("课程材料")
public class CourseMaterialVO {

    @ApiModelProperty("材料ID")
    private Long id;

    @ApiModelProperty("类型 plan|slides|toolkit|video")
    private String type;

    @ApiModelProperty("材料名")
    private String name;

    @ApiModelProperty("下载地址（未上传时为空，前端按未上传态提示）")
    private String url;

    @ApiModelProperty("展示用大小")
    private String size;
}
