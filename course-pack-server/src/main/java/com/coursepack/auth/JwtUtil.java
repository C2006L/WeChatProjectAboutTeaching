package com.coursepack.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 生成与解析（jjwt 0.12.x）
 * 载荷携带 uid / role / status，7 天过期，允许两端同时在线（无状态，不互踢）。
 */
@Component
public class JwtUtil {

    /** HS256 要求密钥长度 >= 32 字节 */
    private static final long EXPIRE_MS = 7L * 24 * 3600 * 1000;

    private final SecretKey key;

    public JwtUtil(@Value("${jwt.secret:}") String secret) {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            // fail-fast：密钥缺失/过短直接拒绝启动，避免弱密钥默认可用
            throw new IllegalStateException("JWT 密钥未配置或长度不足 32 字节，请在 application-local.yml 或环境变量 JWT_SECRET 中配置");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(Long uid, String role, String status) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(uid))
                .claim("role", role)
                .claim("status", status)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + EXPIRE_MS))
                .signWith(key)
                .compact();
    }

    /**
     * 解析 token，返回 claims；失败/过期抛 JwtException
     */
    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}