package com.coursepack.dto.training;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 管理端培训聚合（批次J：口径与 teacher_training 记录实时对齐）
 */
@Data
@ApiModel("培训管理聚合")
public class TrainingOverviewVO {

    @ApiModelProperty("必修完成率 %")
    private Integer requiredRate;

    @ApiModelProperty("已完成全部必修的教师数")
    private Integer doneTeachers;

    @ApiModelProperty("进行中教师数")
    private Integer inprogressTeachers;

    @ApiModelProperty("覆盖队伍数（批次J4 后接真实 team 关联）")
    private Integer teamCount;

    @ApiModelProperty("未完成必修教师名单")
    private List<UnfinishedRow> unfinished;

    @Data
    @ApiModel("未完成必修教师行")
    public static class UnfinishedRow {
        @ApiModelProperty("教师 id")
        private Long teacherId;
        @ApiModelProperty("姓名")
        private String name;
        @ApiModelProperty("队伍（批次J4 前：未分配）")
        private String team;
        @ApiModelProperty("账号状态 PENDING/ACTIVE/REJECTED/DISABLED")
        private String status;
        @ApiModelProperty("已完成必修数")
        private Integer completedCount;
        @ApiModelProperty("必修总数")
        private Integer requiredCount;
        @ApiModelProperty("未完成必修课程名")
        private List<String> missing;
    }
}
