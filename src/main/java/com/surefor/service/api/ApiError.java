package com.surefor.service.api;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class ApiError {
    private static final String UNKNOWN = "unknown";
    private HttpStatus status = null;
    private String timestamp;
    private String message = null;
    private String requestId = null;

    private ApiError() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        timestamp = LocalDateTime.now().format(formatter);
    }

    public ApiError(final HttpStatus status) {
        this();
        this.status = status;
        this.message = "Unexpected error";
    }

    public ApiError(final HttpStatus status, final String message) {
        this(status);
        this.message = message;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ApiError: " + (message != null ? message : UNKNOWN) + "\n")
                .append("requestId: " + (requestId != null ? requestId : UNKNOWN) + "\n")
                .append("HttpStatus: " + (status != null ? status.toString() : UNKNOWN) + "\n")
                .append("timestamp: " + (timestamp != null ? timestamp : UNKNOWN));
        return sb.toString();
    }
}
