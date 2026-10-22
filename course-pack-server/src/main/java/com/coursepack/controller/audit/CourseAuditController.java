package com.coursepack.controller.audit;

import com.coursepack.auth.RequireRole;
import com.coursepack.common.Result;
import com.coursepack.common.UserRole;
import com.coursepack.dto.admin.RejectRequest;
import com.coursepack.dto.audit.CourseAuditVO;
import com.coursepack.service.CourseAuditService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 课程审核中心（AUDITOR + ADMIN）：队列按审核员 audit_field 过滤，管理员看全量。
 */
@Api(tags = "审核中心-课程审核")
@RestController
@RequestMapping("/api/audit")
@RequireRole({UserRole.AUDITOR, UserRole.ADMIN})
public class CourseAuditController {

    private final CourseAuditService auditService;

    public CourseAuditController(CourseAuditService auditService) {
        this.auditService = auditService;
    }

    @ApiOperation("当前审核员待审队列（按 audit_field 过滤）")
    @GetMapping("/queue")
    public Result<List<CourseAuditVO>> queue() {
        return Result.success(auditService.queue());
    }

    @ApiOperation("通过课程审核")
    @PostMapping("/{id}/approve")
    public Result<Void> approve(@PathVariable Long id) {
        auditService.approve(id);
        return Result.success();
    }

    @ApiOperation("驳回课程审核（附原因）")
    @PostMapping("/{id}/reject")
    public Result<Void> reject(@PathVariable Long id, @Validated @RequestBody RejectRequest req) {
        auditService.reject(id, req.getReason());
        return Result.success();
    }
}