package com.manage.cattle.advice;

import com.manage.cattle.exception.BusinessException;
import com.manage.cattle.exception.LoginException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ControllerAdvice
public class ExceptionAdvice {
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseEntity<Object> handleException(Exception e) {
        log.error("系统异常", e);
        return new ResponseEntity<>("系统异常", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = LoginException.class)
    @ResponseBody
    public ResponseEntity<Object> handleException(LoginException e) {
        log.error("登录异常", e);
        // 返回统一的响应格式
        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = BusinessException.class)
    @ResponseBody
    public ResponseEntity<Object> handleException(BusinessException e) {
        log.error("业务异常", e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
