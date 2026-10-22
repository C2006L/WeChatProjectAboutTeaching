package com.coursepack.dto.admin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理端看板聚合（只返回后端真实可计算的部分；前端用它覆盖 mock 基底）
 * 覆盖范围：welcome 待办数 / stats 四卡 / growth 趋势 / themes 主题分布 / todos 待办 / activities 动态。
 * 队伍/培训/案例等域批次J 前仍由前端 mock 兜底。
 */
@Data
@ApiModel("管理端看板聚合")
public class DashboardVO {

    @ApiModelProperty("待课程审核数")
    private Integer pendingAudits;

    @ApiModelProperty("待教师审核数")
    private Integer pendingTeachers;

    @ApiModelProperty("待办总数")
    private Integer todoCount;

    @ApiModelProperty("累计教师数（TEACHER）")
    private Integer teacherCount;

    @ApiModelProperty("已上线课程数")
    private Integer courseOnlineCount;

    @ApiModelProperty("累计收藏数（学习热度代理指标）")
    private Integer favoriteCount;

    @ApiModelProperty("近半年教师增长趋势（按月累计，last 6）")
    private List<GrowthPoint> growth;

    @ApiModelProperty("已上线课程主题分布")
    private List<ThemeCount> themes;

    @ApiModelProperty("最新动态（timeline 最新 10 条）")
    private List<TimelineLogVO> activities;

    @Data
    @ApiModel("增长趋势点")
    public static class GrowthPoint {
        @ApiModelProperty("月份 yyyy-MM")
        private String month;
        @ApiModelProperty("累计教师数")
        private Integer teachers;
    }

    @Data
    @ApiModel("主题分布")
    public static class ThemeCount {
        @ApiModelProperty("主题中文名")
        private String theme;
        @ApiModelProperty("课程数")
        private Integer value;
        @ApiModelProperty("占比 1-100")
        private Integer percent;
    }
}
