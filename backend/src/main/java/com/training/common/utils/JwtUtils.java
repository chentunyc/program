package com.training.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * JWT工具类
 * 用于生成和解析JWT Token
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "training.jwt")
public class JwtUtils {

    /**
     * 密钥
     */
    private String secret;

    /**
     * 过期时间(秒)
     */
    private Long expiration;

    /**
     * Token请求头名称
     */
    private String header;

    /**
     * Token前缀
     */
    private String prefix;

    /**
     * 生成Token
     *
     * @param claims 自定义声明
     * @return Token字符串
     */
    public String generateToken(Map<String, Object> claims) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration * 1000);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSecretKey())
                .compact();
    }

    /**
     * 从Token中获取Claims
     *
     * @param token Token字符串
     * @return Claims
     */
    public Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("解析Token失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从Token中获取用户ID
     *
     * @param token Token字符串
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        if (claims == null) {
            return null;
        }
        return claims.get("userId", Long.class);
    }

    /**
     * 从Token中获取用户名
     *
     * @param token Token字符串
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        if (claims == null) {
            return null;
        }
        return claims.get("username", String.class);
    }

    /**
     * 验证Token是否有效
     *
     * @param token Token字符串
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            if (claims == null) {
                return false;
            }
            Date expiration = claims.getExpiration();
            return expiration != null && expiration.after(new Date());
        } catch (Exception e) {
            log.error("Token验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 从Token字符串中提取Token(去除前缀)
     *
     * @param tokenStr Token字符串(可能包含前缀)
     * @return 纯Token
     */
    public String extractToken(String tokenStr) {
        if (tokenStr != null && tokenStr.startsWith(prefix)) {
            return tokenStr.substring(prefix.length());
        }
        return tokenStr;
    }

    /**
     * 获取密钥对象
     */
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
