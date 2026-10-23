package com.coursepack.controller.training;

import com.coursepack.auth.AuthContext;
import com.coursepack.auth.RequireRole;
import com.coursepack.common.Result;
import com.coursepack.common.UserRole;
import com.coursepack.dto.training.TrainingVO;
import com.coursepack.service.TrainingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 教师端培训（批次J，仅 TEACHER）：课程包/学习/进度上报。
 */
@Api(tags = "教师端-培训")
@RestController
@RequestMapping("/api/trainings")
@RequireRole(UserRole.TEACHER)
public class TrainingController {

    private final TrainingService trainingService;

    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @ApiOperation("培训列表（含个人进度）")
    @GetMapping
    public Result<List<TrainingVO>> list() {
        return Result.success(trainingService.listForUser(AuthContext.userId()));
    }

    @ApiOperation("培训详情")
    @GetMapping("/{id}")
    public Result<TrainingVO> detail(@PathVariable Long id) {
        return Result.success(trainingService.getForUser(AuthContext.userId(), id));
    }

    @ApiOperation("上报学习进度 {progress: 0-100}")
    @PostMapping("/{id}/progress")
    public Result<Void> progress(@PathVariable Long id, @RequestBody(required = false) Map<String, Integer> body) {
        trainingService.updateProgress(AuthContext.userId(), id, body == null ? null : body.get("progress"));
        return Result.success();
    }
}
