package com.surefor.service.cucumber.dto;

import com.surefor.service.domain.user.UserRoleType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UUIDResponse {
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
}
