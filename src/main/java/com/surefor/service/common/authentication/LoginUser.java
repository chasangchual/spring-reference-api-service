package com.surefor.service.common.authentication;

import com.surefor.service.domain.user.UserRoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class LoginUser {
    private UUID publicId;
    private List<UserRoleType> roles;
}
