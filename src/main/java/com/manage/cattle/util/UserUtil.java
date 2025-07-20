package com.manage.cattle.util;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTHeader;
import cn.hutool.jwt.JWTUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.poi.ss.formula.functions.T;

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
        payload.put("create_time", now.toString());
        payload.put("expire_time", now.offsetNew(DateField.HOUR, 1).toString());
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
        DateTime now = DateTime.now();
        String expireTime = (String) getPayloadVal(token, "expire_time");
        return expireTime.compareTo(now.toString()) > 0;
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
