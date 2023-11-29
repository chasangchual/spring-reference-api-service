package com.surefor.service.api.controller.v1;

import com.surefor.service.api.dto.UserLoginDto;
import com.surefor.service.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Endpoints for the MTS (Time Meets Soul) user authentication")
@RestController
@Slf4j
@RequestMapping("/member/v1")
@RequiredArgsConstructor
public class UserAuthorizeController {

    private final UserService userService;


    @Operation(summary = "TMS user sign-up with email and password")
    @PostMapping("/authorize")
    public ResponseEntity<UserLoginDto.Response> authorize(@RequestBody @Valid UserLoginDto.Request request) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.login(request.getEmail(), request.getPassword()));
    }
}
