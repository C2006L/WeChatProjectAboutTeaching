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

    public JwtUtil(@Value("${jwt.secret:course-pack-jwt-secret-banya-2026-0123456789abcdef}") String secret) {
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