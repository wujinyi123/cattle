package com.manage.cattle.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Calendar;
import java.util.Map;

public class JWTUtil {
    //秘钥
    private static final String SIGNATURE = "com.manage.cattle";
    //过期时间为1小时
    public static final Integer EXPIRATION_TIME = 60 * 60;

    /**
     * 生成token
     *
     * @param payload token需要携带的信息
     * @return token字符串
     */
    public static String createToken(Map<String, String> payload) {
        // 指定token过期时间为1天
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, EXPIRATION_TIME);
        JWTCreator.Builder builder = JWT.create();
        // 构建payload
        payload.forEach(builder::withClaim);
        // 指定过期时间和签名算法
        return builder.withExpiresAt(calendar.getTime()).sign(Algorithm.HMAC256(SIGNATURE));
    }

    /**
     * 获取token
     *
     * @return String
     */
    public static String getToken() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attrs.getRequest();
        return getToken(request);
    }

    /**
     * 获取token
     *
     * @param request request
     * @return String
     */
    public static String getToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                String value = cookie.getValue();
                if ("token".equals(name) && StringUtils.isNotBlank(value)) {
                    return value;
                }
            }
        }
        String token = request.getHeader("token");
        if (StringUtils.isNotBlank(token)) {
            return token;
        }
        return request.getParameter("token");
    }

    /**
     * 验证token
     *
     * @param token token
     */
    public static void verify(String token) {
        JWT.require(Algorithm.HMAC256(SIGNATURE)).build().verify(token);
    }

    /**
     * 获取token中payload
     *
     * @param token token
     * @return DecodedJWT
     */
    public static DecodedJWT getPayload(String token) {
        return JWT.require(Algorithm.HMAC256(SIGNATURE)).build().verify(token);
    }

    /**
     * 当前用户账号
     *
     * @return String
     */
    public static String getUsername() {
        String token = getToken();
        DecodedJWT decodedJWT = getPayload(token);
        return decodedJWT.getPayload();
    }
}
