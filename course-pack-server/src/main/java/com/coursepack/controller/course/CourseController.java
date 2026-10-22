package com.coursepack.controller.course;

import com.coursepack.auth.AuthContext;
import com.coursepack.auth.RequireRole;
import com.coursepack.common.Result;
import com.coursepack.common.UserRole;
import com.coursepack.dto.course.CourseMaterialVO;
import com.coursepack.dto.course.CourseVO;
import com.coursepack.service.CourseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 课程浏览（批次H，登录即可：教师主用，审核员/管理员亦可浏览）
 */
@Api(tags = "课程-浏览")
@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @ApiOperation("已上架课程全量（前端筛选，对齐 mock 行为）")
    @GetMapping
    public Result<List<CourseVO>> list() {
        return Result.success(courseService.listOnline());
    }

    @ApiOperation("课程详情")
    @GetMapping("/{id}")
    public Result<CourseVO> detail(@PathVariable Long id) {
        return Result.success(courseService.onlineDetail(id));
    }

    @ApiOperation("课程材料列表")
    @GetMapping("/{id}/materials")
    public Result<List<CourseMaterialVO>> materials(@PathVariable Long id) {
        return Result.success(courseService.materials(id));
    }

    @ApiOperation("收藏 toggle（仅 TEACHER）：返回切换后状态")
    @PostMapping("/{id}/favorite")
    @RequireRole(UserRole.TEACHER)
    public Result<Boolean> favorite(@PathVariable Long id) {
        return Result.success(courseService.toggleFavorite(AuthContext.userId(), id));
    }
}
