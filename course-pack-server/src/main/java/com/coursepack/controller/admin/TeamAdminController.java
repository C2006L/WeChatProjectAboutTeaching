package com.coursepack.controller.admin;

import com.coursepack.auth.RequireRole;
import com.coursepack.common.Result;
import com.coursepack.common.UserRole;
import com.coursepack.dto.admin.TeamVO;
import com.coursepack.service.TeamResourceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理端支教队伍（批次J4，仅 ADMIN）
 */
@Api(tags = "管理端-支教队伍")
@RestController
@RequestMapping("/api/admin/teams")
@RequireRole(UserRole.ADMIN)
public class TeamAdminController {

    private final TeamResourceService service;

    public TeamAdminController(TeamResourceService service) {
        this.service = service;
    }

    @ApiOperation("队伍列表（成员必修进度实时聚合）")
    @GetMapping
    public Result<List<TeamVO>> list() {
        return Result.success(service.adminTeams());
    }
}
