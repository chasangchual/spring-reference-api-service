package com.surefor.service.common.exception;

import org.apache.http.HttpStatus;

public enum TMSException {
    INVALID_TOKEN(HttpStatus.SC_UNAUTHORIZED, "TK001", "Invalid token"),
    EXPIRED_TOKEN(HttpStatus.SC_UNAUTHORIZED, "TK002", "Expired token"),
    ACCESS_DENIED(HttpStatus.SC_FORBIDDEN, "TK003", "Expired token"),
    UNAUTHORIZED_USER(HttpStatus.SC_UNAUTHORIZED, "TK004", "Authentication failure"),
    INVALID_API_KEY(HttpStatus.SC_UNAUTHORIZED, "TK005", "Invalid API key"),
    INVALID_CREDENTIAL(HttpStatus.SC_UNAUTHORIZED, "ATH001", "Invalid login credential"),

    INVALID_USER_STATE(HttpStatus.SC_UNAUTHORIZED, "ATH002", "User is not in valida state"),
    INTERNAL_ERROR(HttpStatus.SC_INTERNAL_SERVER_ERROR, "IE001", "Internal server error");

    private final int httpStatusCode;
    private final String code;
    private final String message;

    TMSException(int httpStatusCode, String code, String message) {
        this.httpStatusCode = httpStatusCode;
        this.code = code;
        this.message = message;
    }

    public int httpStatusCode() {
        return this.httpStatusCode;
    }

    public String code() {
        return this.code;
    }

    public String message() {
        return this.message;
    }
}
