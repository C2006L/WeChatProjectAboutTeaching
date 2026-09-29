package com.coursepack.auth;

import com.coursepack.common.BusinessException;
import com.coursepack.common.ErrorCode;
import com.coursepack.common.UserRole;
import com.coursepack.common.UserStatus;
import com.coursepack.entity.User;
import com.coursepack.mapper.UserMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * 统一认证拦截器：校验 JWT → 写入 AuthContext → 按 @RequireRole 做角色校验。
 * 同时做状态机兜底：仍为 DISABLED/REJECTED 的账号一律拦截（即便 token 未过期）。
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;

    public AuthInterceptor(JwtUtil jwtUtil, UserMapper userMapper) {
        this.jwtUtil = jwtUtil;
        this.userMapper = userMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod hm = (HandlerMethod) handler;

        String auth = request.getHeader("Authorization");
        if (!StringUtils.hasText(auth) || !auth.startsWith("Bearer ")) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Claims claims;
        try {
            claims = jwtUtil.parse(auth.substring(7));
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.TOKEN_EXPIRED);
        }

        Long uid = Long.valueOf(claims.getSubject());
        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getId, uid));
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        // 状态机兜底：停用/驳回一律拦截
        if (UserStatus.DISABLED.name().equals(user.getStatus())
                || UserStatus.REJECTED.name().equals(user.getStatus())) {
            throw new BusinessException(ErrorCode.USER_DISABLED);
        }

        AuthContext.set(new AuthContext.CurrentUser(
                user.getId(), user.getUsername(), user.getName(), user.getRole(), user.getStatus(),
                user.getIsSeed(), user.getAuditField()));

        // 角色校验
        RequireRole need = hm.getMethodAnnotation(RequireRole.class);
        if (need == null) {
            need = hm.getBeanType().getAnnotation(RequireRole.class);
        }
        if (need != null) {
            UserRole currentRole = UserRole.valueOf(user.getRole());
            boolean ok = Arrays.asList(need.value()).contains(currentRole);
            if (!ok) {
                throw new BusinessException(ErrorCode.NO_PERMISSION);
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        AuthContext.clear();
    }
}