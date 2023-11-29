package com.surefor.service.domain.user.service;

import com.surefor.service.domain.user.repository.TmsUserRepository;
import com.surefor.service.common.exception.TMSException;
import com.surefor.service.common.exception.TMSPlatformException;
import com.surefor.service.domain.base.service.ServiceBase;
import com.surefor.service.domain.user.entity.CustomUserDetails;
import com.surefor.service.domain.user.entity.User;
import com.surefor.service.domain.user.entity.UserRole;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.stream.Collectors;

/**
 * TODO: implement user authentication flow
 */
@Service
public class CustomUserDetailsService extends ServiceBase implements UserDetailsService {

    private final TmsUserRepository tmsUserRepository;

    @Autowired
    public CustomUserDetailsService(final ModelMapper modelMapper,
                                    final TmsUserRepository tmsUserRepository) {
        super(modelMapper);

        this.tmsUserRepository = tmsUserRepository;
    }

    @Override
    public CustomUserDetails loadUserByUsername(String publicId) throws UsernameNotFoundException {
        User user = tmsUserRepository.findByPublicId(UUID.fromString(publicId)).orElseThrow(() -> new TMSPlatformException(TMSException.UNAUTHORIZED_USER));
        return CustomUserDetails.builder()
                .roles(user.getRoles().stream().map(UserRole::getRole).collect(Collectors.toList()))
                .publicId(user.getPublicId())
                .userState(user.getState())
                .expired(Boolean.TRUE)
                .build();
    }
}
