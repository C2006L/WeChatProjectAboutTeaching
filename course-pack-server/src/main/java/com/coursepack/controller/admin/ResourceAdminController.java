package com.coursepack.controller.admin;

import com.coursepack.auth.AuthContext;
import com.coursepack.auth.RequireRole;
import com.coursepack.common.Result;
import com.coursepack.common.UserRole;
import com.coursepack.dto.admin.ResourceVO;
import com.coursepack.service.TeamResourceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理端资源库（批次J4，仅 ADMIN）：列表 + 模拟下载（次数+1，写时间线）
 */
@Api(tags = "管理端-资源库")
@RestController
@RequestMapping("/api/admin/resources")
@RequireRole(UserRole.ADMIN)
public class ResourceAdminController {

    private final TeamResourceService service;

    public ResourceAdminController(TeamResourceService service) {
        this.service = service;
    }

    @ApiOperation("资源列表")
    @GetMapping
    public Result<List<ResourceVO>> list() {
        return Result.success(service.adminResources());
    }

    @ApiOperation("下载资源（次数+1，返回最新条目）")
    @PostMapping("/{id}/download")
    public Result<ResourceVO> download(@PathVariable Long id) {
        return Result.success(service.download(id, AuthContext.userId()));
    }
}
