package com.surefor.service.api.controller.v1;

import com.surefor.service.api.dto.UserSignupDto;
import com.surefor.service.common.authentication.LoginUser;
import com.surefor.service.domain.user.service.UserService;
import com.surefor.service.api.dto.StringResponseDto;
import com.surefor.service.common.annotation.Auth;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "Registration endpoints to create TMS user account")
@RestController
@Slf4j
@RequestMapping("/member/v1")
@RequiredArgsConstructor
public class UserSignUpController {

    private final UserService userService;

    @Operation(summary = "Sign-up with a email and a password")
    @PostMapping("/signup")
    public ResponseEntity<StringResponseDto.Response> signup(@Parameter(hidden = true) @Auth LoginUser loginUser,
                                                             @RequestBody UserSignupDto.Request request) {
        UUID publicId = userService.createTimUser(request.getEmail(), request.getPassword(), request.getRole());
        return ResponseEntity.status(HttpStatus.CREATED).body(StringResponseDto.Response.builder().value(publicId.toString()).build());
    }
}
