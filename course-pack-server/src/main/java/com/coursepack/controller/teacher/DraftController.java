package com.coursepack.controller.teacher;

import com.coursepack.auth.AuthContext;
import com.coursepack.auth.RequireRole;
import com.coursepack.common.UserRole;
import com.coursepack.common.Result;
import com.coursepack.entity.LessonDraft;
import com.coursepack.service.LessonDraftService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 备课草稿（教师角色）：电脑备课、手机续写，读写同一张表。
 */
@Api(tags = "教师工具-备课草稿")
@RestController
@RequestMapping("/api/tools/prepare")
@RequireRole(UserRole.TEACHER)
public class DraftController {

    private final LessonDraftService draftService;

    public DraftController(LessonDraftService draftService) {
        this.draftService = draftService;
    }

    @ApiOperation("取当前教师最新草稿")
    @GetMapping("/draft")
    public Result<LessonDraft> getDraft() {
        return Result.success(draftService.getLatest(AuthContext.userId()));
    }

    @ApiOperation("保存草稿（存在则更新）")
    @PostMapping("/draft")
    public Result<LessonDraft> saveDraft(@RequestBody LessonDraft draft) {
        draft.setUserId(AuthContext.userId());
        return Result.success(draftService.save(draft));
    }
}