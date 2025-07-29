package com.manage.cattle.interceptor;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.manage.cattle.util.UserUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
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
        ResponseEntity<Object> responseEntity = null;
        //获取令牌token
        String token = UserUtil.getToken(request);
        if (StrUtil.isBlank(token)) {
            responseEntity = new ResponseEntity<>("token为空", HttpStatus.UNAUTHORIZED);
        } else{
            String error = UserUtil.verify(token);
            if (StrUtil.isNotBlank(error)) {
                responseEntity = new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
            }
        }
        if (responseEntity != null) {
            //将map转为json，返回给前端
            String json = new ObjectMapper().writeValueAsString(responseEntity);
            response.setContentType("application/json;charset=utf-8");
            response.setStatus(responseEntity.getStatusCode().value());
            response.getWriter().write(json);
        }
        return responseEntity == null;
    }
}
