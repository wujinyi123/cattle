package com.manage.cattle.interceptor;

import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.manage.cattle.util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * jwt拦截器
 */
@Slf4j
@Component
public class JWTInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取令牌token
        String token = JWTUtil.getToken(request);
        ResponseEntity<Object> responseEntity;
        try {
            if (StringUtils.isNotBlank(token)) {
                //验证令牌
                JWTUtil.verify(token);
                return true;
            } else {
                responseEntity = new ResponseEntity<>("token为空", HttpStatus.UNAUTHORIZED);
            }
        } catch (SignatureVerificationException | AlgorithmMismatchException e) {
            log.error("无效token", e);
            responseEntity = new ResponseEntity<>("无效token", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("token已过期", e);
            responseEntity = new ResponseEntity<>("token已过期", HttpStatus.UNAUTHORIZED);
        }
        //将map转为json，返回给前端
        String json = new ObjectMapper().writeValueAsString(responseEntity);
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(responseEntity.getStatusCode().value());
        response.getWriter().write(json);
        return false;
    }
}
