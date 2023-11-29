package com.surefor.service.common.exception;

import lombok.Getter;

@Getter
public class TMSPlatformException extends RuntimeException {
    private final TMSException exception;
    private final int httpStatusCode;
    private final String code;
    private final String message;

    public TMSPlatformException(TMSException exception) {
        super(exception.message());

        this.exception = exception;
        this.httpStatusCode = exception.httpStatusCode();
        this.code = exception.code();
        this.message = String.format("[%s] %s", exception.code(), exception.message());
    }
}
