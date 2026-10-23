package com.coursepack.dto.training;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 教师端培训课程视图（对齐客户端 Training 类型）
 */
@Data
@ApiModel("教师培训课程")
public class TrainingVO {

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("required 必修 | elective 选修")
    private String type;

    @ApiModelProperty("时长（分钟）")
    private Integer duration;

    @ApiModelProperty("学习进度 0-100")
    private Integer progress;

    @ApiModelProperty("done 已完成 | current 进行中 | lock 未解锁")
    private String status;

    @ApiModelProperty("视频地址（空=占位）")
    private String videoUrl;

    @ApiModelProperty("导语")
    private String introduction;

    @ApiModelProperty("大纲")
    private List<String> outline;

    @ApiModelProperty("附件材料")
    private List<MaterialItem> materials;

    @Data
    @ApiModel("培训附件材料")
    public static class MaterialItem {
        @ApiModelProperty("文件名")
        private String name;
        @ApiModelProperty("类型 pdf/ppt/img")
        private String type;
    }
}
