package com.surefor.service.api.dto;

import lombok.*;

public class StringResponseDto {
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @Builder
    public static class Response {
        private String value;
    }
}
