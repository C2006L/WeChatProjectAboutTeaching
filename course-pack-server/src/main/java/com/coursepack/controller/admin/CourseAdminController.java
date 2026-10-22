package com.coursepack.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.coursepack.auth.AuthContext;
import com.coursepack.auth.RequireRole;
import com.coursepack.common.PageResult;
import com.coursepack.common.Result;
import com.coursepack.common.UserRole;
import com.coursepack.dto.course.CourseUpsertRequest;
import com.coursepack.dto.course.CourseVO;
import com.coursepack.service.CourseService;
import com.coursepack.service.TimelineService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 课程管理（批次H，仅 ADMIN）：CRUD + 关键字/状态筛选，写操作进时间线。
 */
@Api(tags = "管理端-课程管理")
@RestController
@RequestMapping("/api/admin/courses")
@RequireRole(UserRole.ADMIN)
public class CourseAdminController {

    private final CourseService courseService;
    private final TimelineService timelineService;

    public CourseAdminController(CourseService courseService, TimelineService timelineService) {
        this.courseService = courseService;
        this.timelineService = timelineService;
    }

    @ApiOperation("课程分页（keyword 匹配标题；status 0/1）")
    @GetMapping
    public Result<PageResult<CourseVO>> list(@RequestParam(required = false) String keyword,
                                             @RequestParam(required = false) Integer status,
                                             @RequestParam(defaultValue = "1") long page,
                                             @RequestParam(defaultValue = "10") long size) {
        Page<CourseVO> p = courseService.adminList(page, size, keyword, status);
        return Result.success(PageResult.of(p.getTotal(), p.getCurrent(), p.getSize(), p.getPages(), p.getRecords()));
    }

    @ApiOperation("课程详情")
    @GetMapping("/{id}")
    public Result<CourseVO> detail(@PathVariable Long id) {
        return Result.success(courseService.adminGet(id));
    }

    @ApiOperation("新增课程")
    @PostMapping
    public Result<CourseVO> create(@Valid @RequestBody CourseUpsertRequest req) {
        CourseVO vo = courseService.create(req);
        timelineService.log("COURSE_CREATED", "新课程《" + vo.getTitle() + "》已创建", AuthContext.userId());
        return Result.success(vo);
    }

    @ApiOperation("更新课程")
    @PutMapping("/{id}")
    public Result<CourseVO> update(@PathVariable Long id, @Valid @RequestBody CourseUpsertRequest req) {
        CourseVO vo = courseService.update(id, req);
        timelineService.log("COURSE_UPDATED", "课程《" + vo.getTitle() + "》已更新", AuthContext.userId());
        return Result.success(vo);
    }

    @ApiOperation("删除课程（逻辑删除）")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        CourseVO vo = courseService.adminGet(id);
        courseService.delete(id);
        timelineService.log("COURSE_DELETED", "课程《" + vo.getTitle() + "》已删除", AuthContext.userId());
        return Result.success();
    }
}
