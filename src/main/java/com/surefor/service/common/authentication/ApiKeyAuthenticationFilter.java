package com.surefor.service.common.authentication;

import com.surefor.service.common.exception.TMSException;
import com.surefor.service.common.exception.TMSPlatformException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Objects;

public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {
    private final ClientAppKeyProvider clientAppKeyProvider;
    private final AuthenticationToken authenticationToken;

    private final HandlerExceptionResolver handlerExceptionResolver;

    public ApiKeyAuthenticationFilter(ClientAppKeyProvider clientAppKeyProvider,
                                      AuthenticationToken authenticationToken,
                                      HandlerExceptionResolver handlerExceptionResolver) {
        this.clientAppKeyProvider = clientAppKeyProvider;
        this.authenticationToken = authenticationToken;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String apiKey = authenticationToken.resolveClientAppKey((HttpServletRequest) request);
        try {
            if (Objects.isNull(apiKey) || Boolean.FALSE.equals(clientAppKeyProvider.isValid(apiKey))) {
                throw new TMSPlatformException(TMSException.INVALID_API_KEY);
            } else {
                ApiKeyAuthenticationToken apiToken = new ApiKeyAuthenticationToken(apiKey, AuthorityUtils.NO_AUTHORITIES);
                SecurityContextHolder.getContext().setAuthentication(apiToken);
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }
}