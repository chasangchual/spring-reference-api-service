package com.surefor.service.enumerator;

public enum ErrorCode {
    ALREADY_REGISTERED("T10001");

    private ErrorCode(final String code) {
        this.code = code;
    }

    private String code;

    public String getCode() {
        return code;
    }
}
