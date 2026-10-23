package com.coursepack.controller.feedback;

import com.coursepack.auth.AuthContext;
import com.coursepack.auth.RequireRole;
import com.coursepack.common.Result;
import com.coursepack.common.UserRole;
import com.coursepack.dto.feedback.TicketCreateRequest;
import com.coursepack.dto.feedback.TicketVO;
import com.coursepack.service.FeedbackService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 教师端问答（批次J，仅 TEACHER）：我的工单 + 提交提问。
 */
@Api(tags = "教师端-问答")
@RestController
@RequestMapping("/api/feedback")
@Validated
@RequireRole(UserRole.TEACHER)
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @ApiOperation("我的问答工单（最新在前）")
    @GetMapping("/tickets")
    public Result<List<TicketVO>> mine() {
        return Result.success(feedbackService.mine(AuthContext.userId()));
    }

    @ApiOperation("提交提问 → PENDING")
    @PostMapping("/tickets")
    public Result<TicketVO> create(@Valid @RequestBody TicketCreateRequest req) {
        return Result.success(feedbackService.create(AuthContext.userId(), req));
    }
}
