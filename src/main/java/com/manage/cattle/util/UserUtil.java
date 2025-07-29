package com.manage.cattle.util;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTHeader;
import cn.hutool.jwt.JWTUtil;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class UserUtil {
    // 环境
    private static String ENV;

    //秘钥
    private static final String SECRET_KEY = "com.manage.cattle";

    @Value("${spring.profiles.include}")
    private String env;

    @PostConstruct
    public void init() {
        ENV = this.env;
    }

    /**
     * 生成token
     *
     * @param payload token需要携带的信息
     * @return token字符串
     */
    public static String createToken(Map<String, Object> payload) {
        DateTime now = DateTime.now();
        payload.put("tokenEnv", ENV);
        payload.put("tokenCreateTime", now.toString());
        payload.put("tokenExpireTime", now.offsetNew(DateField.HOUR, 1).toString());
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
    public static String verify(String token) {
        String tokenEnv = getPayloadVal(token, "tokenEnv");
        if (!StrUtil.equals(tokenEnv, ENV)) {
            return "token无效";
        }
        DateTime now = DateTime.now();
        String tokenExpireTime = getPayloadVal(token, "tokenExpireTime");
        if (StrUtil.isBlank(tokenExpireTime) || now.toString().compareTo(tokenExpireTime) > 0) {
            return "token已过期";
        }
        return "";
    }

    public static String getCurrentUsername() {
        return getPayloadVal("username");
    }

    public static String getIsSysAdmin() {
        return getPayloadVal("isSysAdmin");
    }

    public static <T> T getPayloadVal(String key) {
        String token = getToken();
        return getPayloadVal(token, key);
    }

    public static <T> T getPayloadVal(String token, String key) {
        JWT jwt = JWTUtil.parseToken(token).setKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        jwt.getHeader(JWTHeader.TYPE);
        return (T) jwt.getPayload(key);
    }
}
