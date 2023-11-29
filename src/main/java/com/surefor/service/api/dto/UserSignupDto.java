package com.surefor.service.api.dto;

import com.surefor.service.domain.user.UserRoleType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

public class UserSignupDto {
    @Getter
    @Setter
    public static class Request {

        @Schema(description = "email that being used as a user name", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        @Email
        private String email;

        @Schema(description = "user password", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        @Size(min = 8, max=32)
        private String password;

        @Schema(description = "user role", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        private UserRoleType role;
    }

    @Getter
    @Setter
    public static class Response {
    }
}
