package com.coursepack.dto.admin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 种子教师荣誉（对齐 SeedHonorRow，案例数服务端聚合）
 */
@Data
@ApiModel("种子教师荣誉")
public class SeedHonorVO {

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("姓名（users.name）")
    private String name;

    @ApiModelProperty("队伍（批次J 起按 team_members 关联推导）")
    private String team;

    @ApiModelProperty("认证时间 yyyy-MM-dd")
    private String certifiedAt;

    @ApiModelProperty("上传案例总数")
    private Integer caseCount;

    @ApiModelProperty("已通过案例数")
    private Integer approvedCount;

    @ApiModelProperty("精选案例数")
    private Integer featuredCount;
}
