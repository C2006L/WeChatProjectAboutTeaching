package com.coursepack.service;

import cn.hutool.crypto.digest.BCrypt;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.coursepack.auth.JwtUtil;
import com.coursepack.common.BusinessException;
import com.coursepack.common.ErrorCode;
import com.coursepack.common.UserStatus;
import com.coursepack.common.UserRole;
import com.coursepack.config.WechatProperties;
import com.coursepack.converter.UserConverter;
import com.coursepack.dto.auth.LoginRequest;
import com.coursepack.dto.auth.LoginResponse;
import com.coursepack.dto.auth.UserVO;
import com.coursepack.entity.User;
import com.coursepack.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 统一账号认证（批次E；批次I 增补微信登录）
 * 小程序与 Web 共用同一套 users 表 + /login，JWT 携带 role + status。
 */
@Slf4j
@Service
public class UserService {

    private static final String JSCODE2SESSION_URL = "https://api.weixin.qq.com/sns/jscode2session"
            + "?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code";

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final UserConverter userConverter;
    private final WechatProperties wechat;

    public UserService(UserMapper userMapper, JwtUtil jwtUtil, UserConverter userConverter,
                       WechatProperties wechat) {
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
        this.userConverter = userConverter;
        this.wechat = wechat;
    }

    public LoginResponse login(LoginRequest req) {
        User user = userMapper.selectOne(
                Wrappers.<User>lambdaQuery().eq(User::getUsername, req.getUsername()));
        if (user == null || !BCrypt.checkpw(req.getPassword(), user.getPassword())) {
            // 统一提示，避免账号枚举
            throw new BusinessException(ErrorCode.PARAM_INVALID, "账号或密码错误");
            }
        // 状态机（两端一致）：DISABLED/REJECTED/PENDING 均不可登录，ACTIVE 放行
        checkLoginStatus(user);

        String token = jwtUtil.createToken(user.getId(), user.getRole(), user.getStatus());
        return new LoginResponse(token, userConverter.toVO(user));
    }

    /**
     * 微信登录（批次I）：code → openid → 查/建账号 → JWT。
     * 新微信用户以 PENDING 状态入账（等待管理员后台审核），复用统一状态机。
     */
    public LoginResponse wxLogin(String code) {
        if (!wechat.configured()) {
            throw new BusinessException(ErrorCode.WX_LOGIN_FAILED, "微信登录未配置，请在 application-local.yml 填写 wechat.appid/secret");
        }

        JSONObject session = fetchOpenid(code);
        String openid = session.getStr("openid");
        if (openid == null || openid.isBlank()) {
            throw new BusinessException(ErrorCode.WX_CODE_INVALID, "微信授权码无效或已过期");
        }

        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getOpenid, openid));
        if (user == null) {
            user = new User();
            user.setUsername("wx_" + openid.substring(Math.max(0, openid.length() - 8)));
            user.setPassword(BCrypt.hashpw(callerlessRandomPassword(), BCrypt.gensalt()));
            user.setName("微信教师");
            user.setRole(UserRole.TEACHER.name());
            user.setStatus(UserStatus.PENDING.name());
            user.setIsSeed(0);
            user.setOpenid(openid);
            userMapper.insert(user);
            log.info("[wx-login] 新微信教师入库 username={} openid={}", user.getUsername(), openid);
        }
        checkLoginStatus(user);

        String token = jwtUtil.createToken(user.getId(), user.getRole(), user.getStatus());
        return new LoginResponse(token, userConverter.toVO(user));
    }

    private JSONObject fetchOpenid(String code) {
        String url = String.format(JSCODE2SESSION_URL, wechat.getAppid(), wechat.getSecret(), code);
        String body;
        try {
            body = HttpUtil.get(url, 5000);
        } catch (Exception e) {
            log.error("[wx-login] 调用微信 jscode2session 失败", e);
            throw new BusinessException(ErrorCode.WX_LOGIN_FAILED, "微信服务连接失败，请稍后重试");
        }
        log.info("[wx-login] jscode2session 响应：{}", body);
        return JSONUtil.parseObj(body);
    }

    /** 登录状态机（两端一致）：DISABLED/REJECTED/PENDING 均不可登录，ACTIVE 放行 */
    private void checkLoginStatus(User user) {
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
    }

    /** 供 /me 使用：按 id 取当前用户 */
    public UserVO getById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        return userConverter.toVO(user);
    }

    private String callerlessRandomPassword() {
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }
}