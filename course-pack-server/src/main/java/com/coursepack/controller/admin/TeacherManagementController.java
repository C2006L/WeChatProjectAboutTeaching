package com.coursepack.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.coursepack.auth.AuthContext;
import com.coursepack.auth.RequireRole;
import com.coursepack.common.BusinessException;
import com.coursepack.common.ErrorCode;
import com.coursepack.common.PageResult;
import com.coursepack.common.Result;
import com.coursepack.common.UserRole;
import com.coursepack.converter.UserConverter;
import com.coursepack.dto.admin.RejectRequest;
import com.coursepack.dto.auth.UserVO;
import com.coursepack.entity.User;
import com.coursepack.mapper.UserMapper;
import com.coursepack.service.TimelineService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 教师管理（仅 ADMIN）：状态筛选 + 通过/驳回/停用，操作写入最新动态时间线。
 */
@Api(tags = "管理端-教师管理")
@RestController
@RequestMapping("/api/admin/teachers")
@RequireRole(UserRole.ADMIN)
public class TeacherManagementController {

    private final UserMapper userMapper;
    private final UserConverter userConverter;
    private final TimelineService timelineService;

    public TeacherManagementController(UserMapper userMapper, UserConverter userConverter,
                                       TimelineService timelineService) {
        this.userMapper = userMapper;
        this.userConverter = userConverter;
        this.timelineService = timelineService;
    }

    @ApiOperation("分页查询教师（按状态筛选）")
    @GetMapping
    public Result<PageResult<UserVO>> list(@RequestParam(required = false) String status,
                                           @RequestParam(required = false) String keyword,
                                           @RequestParam(defaultValue = "1") long page,
                                           @RequestParam(defaultValue = "10") long size) {
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.eq("role", UserRole.TEACHER.name());
        if (StringUtils.hasText(status)) {
            qw.eq("status", status);
        }
        if (StringUtils.hasText(keyword)) {
            qw.and(w -> w.like("username", keyword).or().like("name", keyword));
        }
        qw.orderByDesc("created_at");
        Page<User> p = userMapper.selectPage(new Page<>(page, size), qw);
        List<UserVO> records = p.getRecords().stream()
                .map(userConverter::toVO).collect(Collectors.toList());
        return Result.success(PageResult.of(p.getTotal(), p.getCurrent(), p.getSize(), p.getPages(), records));
    }

    @ApiOperation("通过教师审核")
    @PostMapping("/{id}/approve")
    public Result<Void> approve(@PathVariable Long id) {
        User u = teacherOrFail(id);
        u.setStatus("ACTIVE");
        u.setRejectReason(null);
        userMapper.updateById(u);
        timelineService.log("TEACHER_APPROVED", "教师 " + u.getName() + " 已通过审核", AuthContext.userId());
        return Result.success();
    }

    @ApiOperation("驳回教师（附原因）")
    @PostMapping("/{id}/reject")
    public Result<Void> reject(@PathVariable Long id, @RequestBody RejectRequest req) {
        User u = teacherOrFail(id);
        u.setStatus("REJECTED");
        u.setRejectReason(req.getReason());
        userMapper.updateById(u);
        timelineService.log("TEACHER_REJECTED", "教师 " + u.getName() + " 被驳回：" + req.getReason(), AuthContext.userId());
        return Result.success();
    }

    @ApiOperation("停用教师")
    @PostMapping("/{id}/disable")
    public Result<Void> disable(@PathVariable Long id) {
        User u = teacherOrFail(id);
        if (u.getId().equals(AuthContext.userId())) {
            throw new BusinessException(ErrorCode.NO_PERMISSION, "不能停用自己");
        }
        u.setStatus("DISABLED");
        userMapper.updateById(u);
        timelineService.log("TEACHER_DISABLED", "教师 " + u.getName() + " 已被停用", AuthContext.userId());
        return Result.success();
    }

    private User teacherOrFail(Long id) {
        User u = userMapper.selectById(id);
        if (u == null || !UserRole.TEACHER.name().equals(u.getRole())) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "教师不存在");
        }
        return u;
    }
}