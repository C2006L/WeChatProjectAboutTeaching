package com.coursepack.controller.admin;

import com.coursepack.auth.RequireRole;
import com.coursepack.common.Result;
import com.coursepack.common.UserRole;
import com.coursepack.dto.admin.SeedHonorVO;
import com.coursepack.service.TeamResourceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理端种子教师荣誉（批次J4，仅 ADMIN）：案例数服务端聚合
 */
@Api(tags = "管理端-种子教师荣誉")
@RestController
@RequestMapping("/api/admin/seed-teachers")
@RequireRole(UserRole.ADMIN)
public class SeedHonorAdminController {

    private final TeamResourceService service;

    public SeedHonorAdminController(TeamResourceService service) {
        this.service = service;
    }

    @ApiOperation("种子教师荣誉列表（认证时间倒序）")
    @GetMapping
    public Result<List<SeedHonorVO>> list() {
        return Result.success(service.adminSeedHonors());
    }
}
