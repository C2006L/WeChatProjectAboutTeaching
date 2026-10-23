package com.coursepack.controller.admin;

import com.coursepack.auth.RequireRole;
import com.coursepack.common.Result;
import com.coursepack.common.UserRole;
import com.coursepack.dto.training.TrainingCourseVO;
import com.coursepack.dto.training.TrainingOverviewVO;
import com.coursepack.service.TrainingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 培训管理（批次J，仅 ADMIN）：课程完成度聚合 + 未完成名单。
 */
@Api(tags = "管理端-培训管理")
@RestController
@RequestMapping("/api/admin/training")
@RequireRole(UserRole.ADMIN)
public class TrainingAdminController {

    private final TrainingService trainingService;

    public TrainingAdminController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @ApiOperation("培训聚合 overview（含未完成名单）")
    @GetMapping("/overview")
    public Result<TrainingOverviewVO> overview() {
        return Result.success(trainingService.adminOverview());
    }

    @ApiOperation("培训课程完成度列表")
    @GetMapping("/courses")
    public Result<List<TrainingCourseVO>> courses() {
        return Result.success(trainingService.adminCourses());
    }
}
