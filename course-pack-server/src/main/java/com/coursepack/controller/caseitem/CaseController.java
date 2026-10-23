package com.coursepack.controller.caseitem;

import com.coursepack.auth.AuthContext;
import com.coursepack.auth.RequireRole;
import com.coursepack.common.Result;
import com.coursepack.common.UserRole;
import com.coursepack.dto.caseitem.CaseUpsertRequest;
import com.coursepack.dto.caseitem.CaseVO;
import com.coursepack.service.CaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 教师端教学案例（批次J，仅 TEACHER）：公共库 / 我的 / 提交与草稿。
 */
@Api(tags = "教师端-教学案例")
@RestController
@RequestMapping("/api/cases")
@RequireRole(UserRole.TEACHER)
public class CaseController {

    private final CaseService caseService;

    public CaseController(CaseService caseService) {
        this.caseService = caseService;
    }

    @ApiOperation("公共案例库（APPROVED）")
    @GetMapping
    public Result<List<CaseVO>> list() {
        return Result.success(caseService.publicList());
    }

    @ApiOperation("我的案例（全部状态）")
    @GetMapping("/mine")
    public Result<List<CaseVO>> mine() {
        return Result.success(caseService.mine(AuthContext.userId()));
    }

    @ApiOperation("案例详情（已通过或本人可见）")
    @GetMapping("/{id}")
    public Result<CaseVO> detail(@PathVariable Long id) {
        return Result.success(caseService.detail(id, AuthContext.userId()));
    }

    @ApiOperation("提交案例（action: DRAFT | PENDING）")
    @PostMapping
    public Result<CaseVO> create(@Valid @RequestBody CaseUpsertRequest req) {
        return Result.success(caseService.upsert(AuthContext.userId(), null, req, true));
    }

    @ApiOperation("更新案例（本人；action: DRAFT | PENDING）")
    @PutMapping("/{id}")
    public Result<CaseVO> update(@PathVariable Long id, @Valid @RequestBody CaseUpsertRequest req) {
        return Result.success(caseService.upsert(AuthContext.userId(), id, req, true));
    }
}
