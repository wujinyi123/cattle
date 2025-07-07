package com.manage.cattle.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class ResponseAdvice implements ResponseBodyAdvice<Object> {
    @Resource
    private ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @SneakyThrows
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof ExceptionAdvice.ErrorResponseEntity entity) {
            response.setStatusCode(entity.getCode());
            return objectMapper.writeValueAsString(new ResponseEntity<>(entity.getMessage(), entity.getCode()));
        }
        if (selectedContentType.equals(MediaType.APPLICATION_OCTET_STREAM) && body instanceof byte[]) {
            return body;
        }
        if (body instanceof ResponseEntity) {
            return body;
        }
        if (body instanceof String) {
            return objectMapper.writeValueAsString(new ResponseEntity<>(body, HttpStatus.OK));
        }
        return new ResponseEntity<>(body, HttpStatus.OK);
    }
}
