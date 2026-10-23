package com.coursepack.controller.admin;

import com.coursepack.auth.AuthContext;
import com.coursepack.auth.RequireRole;
import com.coursepack.common.Result;
import com.coursepack.common.UserRole;
import com.coursepack.dto.caseitem.CaseAdminVO;
import com.coursepack.service.CaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 管理端案例管理（批次J，仅 ADMIN）：列表 + 精选切换（写时间线，批K 补审核 UI）。
 */
@Api(tags = "管理端-教学案例")
@RestController
@RequestMapping("/api/admin/cases")
@RequireRole(UserRole.ADMIN)
public class CaseAdminController {

    private final CaseService caseService;

    public CaseAdminController(CaseService caseService) {
        this.caseService = caseService;
    }

    @ApiOperation("案例全量列表（更新时间倒序）")
    @GetMapping
    public Result<List<CaseAdminVO>> list() {
        return Result.success(caseService.adminList());
    }

    @ApiOperation("切换精选，返回切换后的值")
    @PostMapping("/{id}/featured")
    public Result<Map<String, Boolean>> toggleFeatured(@PathVariable Long id) {
        boolean next = caseService.toggleFeatured(id, AuthContext.userId());
        return Result.success(Map.of("featured", next));
    }
}
