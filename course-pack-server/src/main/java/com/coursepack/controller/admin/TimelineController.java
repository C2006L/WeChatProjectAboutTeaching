package com.coursepack.controller.admin;

import com.coursepack.auth.RequireRole;
import com.coursepack.common.Result;
import com.coursepack.common.UserRole;
import com.coursepack.converter.TimelineLogConverter;
import com.coursepack.dto.admin.TimelineLogVO;
import com.coursepack.entity.TimelineLog;
import com.coursepack.service.TimelineService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 最新动态时间线（ADMIN / AUDITOR 只读）：看板"最新动态"数据源。
 */
@Api(tags = "管理端-最新动态")
@RestController
@RequestMapping("/api/admin/timeline")
@RequireRole({UserRole.ADMIN, UserRole.AUDITOR})
public class TimelineController {

    private final TimelineService timelineService;
    private final TimelineLogConverter converter;

    public TimelineController(TimelineService timelineService, TimelineLogConverter converter) {
        this.timelineService = timelineService;
        this.converter = converter;
    }

    @ApiOperation("最新动态（按时间倒序）")
    @GetMapping
    public Result<List<TimelineLogVO>> latest(@RequestParam(defaultValue = "10") int limit) {
        List<TimelineLog> logs = timelineService.latest(Math.min(Math.max(limit, 1), 50));
        return Result.success(converter.toVOList(logs));
    }
}
