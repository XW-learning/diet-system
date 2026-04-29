package com.xw.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类
 * 负责 Token 的生成、解析和校验
 *
 * @author XW
 */
public class JwtUtil {

    /** 签名密钥（生产环境应通过环境变量注入，至少 256 位） */
    private static final String SECRET = "diet-system-secret-key-2024-xw-graduation-design-project";
    /** Token 过期时间：7 天 */
    private static final long EXPIRATION = 1000L * 60 * 60 * 24 * 7;

    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    /**
     * 生成普通用户 Token
     *
     * @param userId 用户 ID
     * @return JWT 字符串
     */
    public static String generateUserToken(Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "user");
        claims.put("userId", userId);
        return createToken(claims);
    }

    /**
     * 生成管理员 Token
     *
     * @param adminId 管理员 ID
     * @return JWT 字符串（不含 admin_token_ 前缀）
     */
    public static String generateAdminToken(Long adminId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "admin");
        claims.put("adminId", adminId);
        return createToken(claims);
    }

    /**
     * 构建 JWT Token
     */
    private static String createToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析 Token 中的声明信息
     *
     * @param token JWT 字符串
     * @return Claims 对象
     */
    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 校验 Token 是否有效（签名正确且未过期）
     *
     * @param token JWT 字符串
     * @return true 有效 / false 无效
     */
    public static boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从 Token 中获取用户 ID（仅普通用户 Token）
     *
     * @param token JWT 字符串
     * @return 用户 ID，如果不是普通用户 Token 则返回 null
     */
    public static Long getUserIdFromToken(String token) {
        try {
            Claims claims = parseToken(token);
            if ("user".equals(claims.get("role"))) {
                Object userId = claims.get("userId");
                if (userId instanceof Integer) {
                    return ((Integer) userId).longValue();
                }
                return (Long) userId;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从 Token 中获取管理员 ID（仅管理员 Token）
     *
     * @param token JWT 字符串
     * @return 管理员 ID，如果不是管理员 Token 则返回 null
     */
    public static Long getAdminIdFromToken(String token) {
        try {
            Claims claims = parseToken(token);
            if ("admin".equals(claims.get("role"))) {
                Object adminId = claims.get("adminId");
                if (adminId instanceof Integer) {
                    return ((Integer) adminId).longValue();
                }
                return (Long) adminId;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 判断 Token 是否为管理员 Token
     */
    public static boolean isAdminToken(String token) {
        try {
            Claims claims = parseToken(token);
            return "admin".equals(claims.get("role"));
        } catch (Exception e) {
            return false;
        }
    }
}
