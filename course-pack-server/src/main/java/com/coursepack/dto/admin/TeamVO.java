package com.coursepack.dto.admin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 支教队伍（对齐管理端 TeamDetailRow：completion 由成员进度推导）
 */
@Data
@ApiModel("支教队伍")
public class TeamVO {

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("队名")
    private String name;

    @ApiModelProperty("所属高校")
    private String org;

    @ApiModelProperty("服务地")
    private String area;

    @ApiModelProperty("资源使用次数")
    private Integer usage;

    @ApiModelProperty("队伍成员")
    private List<MemberRow> members;

    @Data
    @ApiModel("队伍成员")
    public static class MemberRow {
        @ApiModelProperty("姓名")
        private String name;
        @ApiModelProperty("队长/成员")
        private String role;
        @ApiModelProperty("已完成必修数（user 关联行实时聚合）")
        private Integer requiredDone;
        @ApiModelProperty("必修总数")
        private Integer requiredTotal;
    }
}
