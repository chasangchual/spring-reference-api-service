package com.surefor.service.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

public class UserLoginDto {
    @Getter
    public static class Request {
        @Schema(description = "email as a user name", example = "myuser@mydomain.com", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        @Email
        private String email;

        @Schema(description = "password to login", example = "Xyz67890!", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        @Size(min = 8, max=32)
        private String password;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        @Schema(description = "access token", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        private String accessToken;

        @Schema(description = "refresh token", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        @Email
        private String refreshToken;

        @Schema(description = "email as a user name", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        private String name;

        @Schema(description = "user identifier for the external services", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        private UUID publicId;

        @Schema(description = "access token expiry in the epoch time", example = "1653527149000", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        private Long refreshTokenReissueAt;

        @Schema(description = "refresh token expiry in the epoch time", example = "1653527149000", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        private Long refreshTokenExpiresAt;
    }
}
