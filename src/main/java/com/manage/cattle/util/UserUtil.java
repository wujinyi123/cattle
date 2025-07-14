package com.manage.cattle.util;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;
import com.manage.cattle.exception.LoginException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class UserUtil {
    //秘钥
    private static final String SECRET_KEY = "com.manage.cattle";

    /**
     * 生成token
     *
     * @param payload token需要携带的信息
     * @return token字符串
     */
    public static String createToken(Map<String, Object> payload) {
        DateTime now = DateTime.now();
        payload.put(JWTPayload.ISSUED_AT, now);
        payload.put(JWTPayload.EXPIRES_AT, now.offsetNew(DateField.HOUR, 1));
        payload.put(JWTPayload.NOT_BEFORE, now);
        return JWTUtil.createToken(payload, SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 获取token
     *
     * @return String
     */
    public static String getToken() {
        return getToken(CommonUtil.getHttpServletRequest());
    }

    /**
     * 获取token
     *
     * @param request request
     * @return String
     */
    public static String getToken(HttpServletRequest request) {
        String token = request.getHeader("token");
        if (StrUtil.isNotBlank(token)) {
            return token;
        }
        token = request.getParameter("token");
        if (StrUtil.isNotBlank(token)) {
            return token;
        }
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                String value = cookie.getValue();
                if ("token".equals(name) && StrUtil.isNotBlank(value)) {
                    return value;
                }
            }
        }
        return "";
    }

    /**
     * 验证token
     *
     * @param token token
     */
    public static boolean verify(String token) {
        JWT jwt = getJwt(token);
        return jwt.verify();
    }

    /**
     * 当前用户账号
     *
     * @return String
     */
    public static String getUsername() {
        JWT jwt = getJwt();
        return jwt.getPayload("username").toString();
    }

    /**
     * token过期时间
     *
     * @return String
     */
    public static String getTokenCreateTime() {
        JWT jwt = getJwt();
        return jwt.getPayload(JWTPayload.ISSUED_AT).toString();
    }

    /**
     * token过期时间
     *
     * @return String
     */
    public static String getTokenExpireTime() {
        JWT jwt = getJwt();
        return jwt.getPayload(JWTPayload.EXPIRES_AT).toString();
    }

    /**
     * getIsSysAdmin
     *
     * @return String
     */
    public static String getIsSysAdmin() {
        JWT jwt = getJwt();
        return jwt.getPayload("isSysAdmin").toString();
    }

    private static JWT getJwt() {
        String token = getToken();
        return getJwt(token);
    }

    private static JWT getJwt(String token) {
        try {
            return JWTUtil.parseToken(token).setKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new LoginException("无效token");
        }
    }
}
