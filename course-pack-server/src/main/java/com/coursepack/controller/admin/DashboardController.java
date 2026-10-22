package com.coursepack.controller.admin;

import com.coursepack.auth.RequireRole;
import com.coursepack.common.Result;
import com.coursepack.common.UserRole;
import com.coursepack.dto.admin.DashboardVO;
import com.coursepack.service.DashboardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端看板聚合（批次H，ADMIN / AUDITOR）：
 * 只返回后端真实可计算的部分（待办/师生计数/趋势/主题分布/动态），
 * 队伍与培训/案例域由前端以 mock 兜底（批次J 补齐）。
 */
@Api(tags = "管理端-数据看板")
@RestController
@RequestMapping("/api/admin/dashboard")
@RequireRole({UserRole.ADMIN, UserRole.AUDITOR})
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @ApiOperation("看板聚合数据")
    @GetMapping
    public Result<DashboardVO> dashboard() {
        return Result.success(dashboardService.build());
    }
}
