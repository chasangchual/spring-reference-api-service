package com.surefor.service.common.authentication;

import com.surefor.service.domain.user.entity.CustomUserDetails;
import com.surefor.service.domain.user.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthenticationToken {
    public final static String API_KEY_HEADER = "X-API-KEY";
    public final static String AUTHORIZATION_HEADER = "Authorization";
    private final CustomUserDetailsService customUserDetailsService;
    /**
     * returns a Spring Security Authentication object with given JWT token
     * @param jwt JWT token
     * @return created Spring Security Authentication object
     */
    public Authentication getAuthentication(@NonNull final String jwt) {
        CustomUserDetails userDetails = customUserDetailsService.loadUserByUsername(JwtTokenProvider.getUserId(jwt).toString());

        if(JwtTokenProvider.getExpiresAt(jwt) < System.currentTimeMillis()) {
            userDetails.nonExpired();
        }

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    /**
     * returns authorization header. <br>
     * The authentication token in the Authentication header of the HTTP request
     * @param httpServletRequest HTTP request object
     * @return Authentication header value
     */
    public static String resolveAuthenticationToken(final HttpServletRequest httpServletRequest) {
        return httpServletRequest.getHeader(AUTHORIZATION_HEADER);
    }

    /**
     * returns the client app key header. <br>
     * The client app key in the CLIENT_KEY_HEADER of the HTTP request
     * @param httpServletRequest HTTP request object
     * @return client app key header value
     */
    public static String resolveClientAppKey(final HttpServletRequest httpServletRequest) {
        return httpServletRequest.getHeader(API_KEY_HEADER);
    }
}
