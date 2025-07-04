package com.manage.cattle.exception;

/**
 * 登录异常
 */
public class LoginException extends RuntimeException {
    public LoginException(String message) {
        super(message);
    }
}
