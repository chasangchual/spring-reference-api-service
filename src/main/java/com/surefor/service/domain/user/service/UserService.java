package com.surefor.service.domain.user.service;

import com.surefor.service.api.dto.UserLoginDto;
import com.surefor.service.domain.user.repository.TmsUserRepository;
import com.surefor.service.domain.user.repository.UserEmailRepository;
import com.surefor.service.domain.user.repository.UserRoleRepository;
import com.surefor.service.common.authentication.JwtTokenProvider;
import com.surefor.service.common.crypto.PasswordEncoder;
import com.surefor.service.common.exception.DomainEntityNotFoundException;
import com.surefor.service.common.exception.TMSException;
import com.surefor.service.common.exception.TMSPlatformException;
import com.surefor.service.domain.base.service.ServiceBase;
import com.surefor.service.domain.user.UserContactType;
import com.surefor.service.domain.user.UserRoleType;
import com.surefor.service.domain.user.UserState;
import com.surefor.service.domain.user.entity.User;
import com.surefor.service.domain.user.entity.UserEmail;
import com.surefor.service.domain.user.entity.UserRole;
import lombok.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService extends ServiceBase {


    private final TmsUserRepository tmsUserRepository;
    private final UserEmailRepository userEmailRepository;
    private final UserRoleRepository userRoleRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserService(TmsUserRepository tmsUserRepository,
                       UserEmailRepository userEmailRepository,
                       UserRoleRepository userRoleRepository,
                       JwtTokenProvider jwtTokenProvider,
                       ModelMapper modelMapper) {
        super(modelMapper);

        this.tmsUserRepository = tmsUserRepository;
        this.userEmailRepository = userEmailRepository;
        this.userRoleRepository = userRoleRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public UUID createTimUser(@NonNull final String email, @NonNull final String password, @NonNull final UserRoleType userRole) {
        User user = User.builder()
                .userName(email)
                .pwhash(PasswordEncoder.encode(password))
                .state(UserState.CREATED)

                .build();
        user = tmsUserRepository.save(user);

        tmsUserRepository.refresh(user);

        UserEmail userEmail = UserEmail.builder()
                .email(email)
                .user(user)
                .isPrimary(Boolean.TRUE)
                .isDeleted(Boolean.FALSE)
                .type(UserContactType.PRIMARY)
                .build();

        userEmailRepository.save(userEmail);

        UserRole role = UserRole.builder()
                .user(user)
                .role(userRole)
                .isDeleted(Boolean.FALSE)
                .build();
        userRoleRepository.save(role);
        return user.getPublicId();
    }

    @Transactional
    public UserLoginDto.Response login(@NonNull final String email, @NonNull final String password) {
        final Long now = System.currentTimeMillis();
        User user = tmsUserRepository.findByUserName(email).orElseThrow(() -> new TMSPlatformException(TMSException.INVALID_CREDENTIAL));

        validatePassword(password, user.getPwhash());
        validateUserState(user.getState(), UserState.CREATED, UserState.CONFIRMED, UserState.ACTIVE);

        final String accessToken = jwtTokenProvider.createAccessToken(user.getPublicId(), user.getRoles().stream().map(e -> e.getRole()).collect(Collectors.toList()), now);
        final String refreshToken = jwtTokenProvider.createRefreshToken(user.getPublicId(), user.getRoles().stream().map(e -> e.getRole()).collect(Collectors.toList()), now);

        final Long refreshTokenReissueAt = now + JwtTokenProvider.ACCESS_TOKEN_EXPIRATION_TIME_MILLIS;
        final Long refreshTokenExpiresAt = now + JwtTokenProvider.REFRESH_TOKEN_EXPIRATION_TIME_MILLIS;

        JwtTokenProvider.getIssuedAtMillis(refreshToken);
        JwtTokenProvider.getExpiresAtMillis(refreshToken);

        return UserLoginDto.Response.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .name(email)
                .publicId(user.getPublicId())
                .refreshTokenReissueAt(refreshTokenReissueAt)
                .refreshTokenExpiresAt(refreshTokenExpiresAt)
                .build();
    }

    private void validatePassword(@NonNull final String password, @NonNull final String expected) {
        if(Boolean.FALSE.equals(PasswordEncoder.matches(password, expected))) {
            throw new TMSPlatformException(TMSException.INVALID_CREDENTIAL);
        }
    }

    private void validateUserState(@NonNull final UserState userState, @NonNull final UserState ... expected) {
        Boolean meetExpectation = Boolean.FALSE;

        for(UserState state : expected) {
            meetExpectation = meetExpectation | userState.equals(state);
        }

        if(Boolean.FALSE.equals(meetExpectation)) {
            throw new TMSPlatformException(TMSException.INVALID_USER_STATE);
        }
    }

    public User getTmsUser(@NonNull final String email) {
        return tmsUserRepository.findByUserName(email).orElseThrow(() -> new DomainEntityNotFoundException("TmsUser", "userName", email));
    }
}
