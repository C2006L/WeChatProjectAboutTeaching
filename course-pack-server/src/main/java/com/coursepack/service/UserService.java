package com.coursepack.service;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.coursepack.auth.JwtUtil;
import com.coursepack.common.BusinessException;
import com.coursepack.common.ErrorCode;
import com.coursepack.common.UserStatus;
import com.coursepack.converter.UserConverter;
import com.coursepack.dto.auth.LoginRequest;
import com.coursepack.dto.auth.LoginResponse;
import com.coursepack.dto.auth.UserVO;
import com.coursepack.entity.User;
import com.coursepack.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
 * 统一账号认证（批次E）
 * 小程序与 Web 共用同一套 users 表 + /login，JWT 携带 role + status。
 */
@Service
public class UserService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final UserConverter userConverter;

    public UserService(UserMapper userMapper, JwtUtil jwtUtil, UserConverter userConverter) {
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
        this.userConverter = userConverter;
    }

    public LoginResponse login(LoginRequest req) {
        User user = userMapper.selectOne(
                Wrappers.<User>lambdaQuery().eq(User::getUsername, req.getUsername()));
        if (user == null || !BCrypt.checkpw(req.getPassword(), user.getPassword())) {
            // 统一提示，避免账号枚举
            throw new BusinessException(ErrorCode.PARAM_INVALID, "账号或密码错误");
            }
        // 状态机（两端一致）：DISABLED/REJECTED/PENDING 均不可登录，ACTIVE 放行
        switch (UserStatus.valueOf(user.getStatus())) {
            case DISABLED:
                throw new BusinessException(ErrorCode.USER_DISABLED);
            case REJECTED:
                throw new BusinessException(ErrorCode.USER_REJECTED);
            case PENDING:
                throw new BusinessException(ErrorCode.USER_PENDING);
            default:
                break;
        }

        String token = jwtUtil.createToken(user.getId(), user.getRole(), user.getStatus());
        return new LoginResponse(token, userConverter.toVO(user));
    }

    /** 供 /me 使用：按 id 取当前用户 */
    public UserVO getById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        return userConverter.toVO(user);
    }
}