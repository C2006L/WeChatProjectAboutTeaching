package com.coursepack.controller.teacher;

import com.coursepack.auth.AuthContext;
import com.coursepack.auth.RequireRole;
import com.coursepack.common.Result;
import com.coursepack.common.UserRole;
import com.coursepack.dto.course.CourseVO;
import com.coursepack.service.CourseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 我的收藏（批次H，仅 TEACHER）：小程序"我的收藏"页数据源。
 */
@Api(tags = "用户-我的收藏")
@RestController
@RequestMapping("/api/user")
@RequireRole(UserRole.TEACHER)
public class UserFavoriteController {

    private final CourseService courseService;

    public UserFavoriteController(CourseService courseService) {
        this.courseService = courseService;
    }

    @ApiOperation("我的收藏课程")
    @GetMapping("/favorites")
    public Result<List<CourseVO>> favorites() {
        return Result.success(courseService.favoritesOf(AuthContext.userId()));
    }
}
