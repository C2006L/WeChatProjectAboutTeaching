package com.coursepack.controller.admin;

import com.coursepack.auth.AuthContext;
import com.coursepack.auth.RequireRole;
import com.coursepack.common.Result;
import com.coursepack.common.UserRole;
import com.coursepack.dto.feedback.TicketReplyRequest;
import com.coursepack.dto.feedback.TicketVO;
import com.coursepack.service.FeedbackService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 管理端问答与反馈（批次J，仅 ADMIN）：全量工单 + 回复（写时间线）。
 */
@Api(tags = "管理端-问答与反馈")
@RestController
@RequestMapping("/api/admin/feedback")
@RequireRole(UserRole.ADMIN)
public class FeedbackAdminController {

    private final FeedbackService feedbackService;

    public FeedbackAdminController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @ApiOperation("全部问答工单（最新在前）")
    @GetMapping("/tickets")
    public Result<List<TicketVO>> list() {
        return Result.success(feedbackService.adminList());
    }

    @ApiOperation("回复工单：PENDING → REPLIED（写时间线）")
    @PostMapping("/tickets/{id}/reply")
    public Result<TicketVO> reply(@PathVariable Long id, @Valid @RequestBody TicketReplyRequest req) {
        return Result.success(feedbackService.reply(id, req.getContent(), AuthContext.userId()));
    }
}
