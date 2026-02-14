package com.itbaizhan.farm_common.exception;

import org.springframework.security.core.AuthenticationException;

public class InvalidCaptchaException extends AuthenticationException {
    public InvalidCaptchaException(String msg) {
        super(msg);
    }
}
