package com.surefor.service.config;

import com.surefor.service.common.authentication.*;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.Collections;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
/**
 * Spring Security configuration <br>
 *
 * @see <a href="https://www.baeldung.com/spring-deprecated-websecurityconfigureradapter">Spring Security Config</a>
 *
 * We commonly see Spring HTTP security configuration classes that extend a WebSecurityConfigureAdapter class. <br>
 * However, beginning with version 5.7.0-M2, Spring deprecates the use of WebSecurityConfigureAdapter and suggests creating configurations without it.
 */
public class WebSecurityConfig {
    Boolean securityDebug = Boolean.TRUE;

    private static final String[] SWAGGER_URI = {
            "/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**"
    };

    private static final String[] SIGNUP_URI = {
            "/member/*/signup",
            "/admin/*/signup"
    };

    private static final String[] LOGIN_URI = {
            "/member/*/authorize",
            "/admin/*/authorize"
    };

    private static final String[] HEALTHCHECK_URI = {
            "/ping"
    };

    private static final String[] STATIC_CONTENTS = {
            "/flutter_service_worker.js",
            "/error"
    };

    private final JwtTokenProvider jwtTokenProvider;

    private final ClientAppKeyProvider clientAppKeyProvider;
    private final AuthenticationToken authenticationToken;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final AccessDeniedHandler accessDeniedHandler;
    private final HandlerExceptionResolver handlerExceptionResolver;

    public WebSecurityConfig(JwtTokenProvider jwtTokenProvider,
                             ClientAppKeyProvider clientAppKeyProvider,
                             AuthenticationToken authenticationToken,
                             AuthenticationEntryPoint authenticationEntryPoint,
                             AccessDeniedHandler accessDeniedHandler,
                             HandlerExceptionResolver handlerExceptionResolver) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.clientAppKeyProvider = clientAppKeyProvider;
        this.authenticationToken = authenticationToken;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }


    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.debug(securityDebug)
                .ignoring()
                .requestMatchers(SWAGGER_URI)
                .requestMatchers(HEALTHCHECK_URI)
                .requestMatchers(STATIC_CONTENTS);
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // disable csrf (cross site request forgery) for REST APIs
                .csrf().disable()
                // disable HTTP basic authentication to avoid redirecting to the out-of-shelf login UI
                .httpBasic().disable()
                .cors().configurationSource(corsConfigurationSource())
                    .and()
                // without sticky session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler)
                    .and()
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
                    .and()
                .addFilterBefore(new ApiKeyAuthenticationFilter(clientAppKeyProvider, authenticationToken, handlerExceptionResolver), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                    .anyRequest().authenticated()
                    .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, authenticationToken, handlerExceptionResolver), UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    /**
     * Cross-Origin Resource Sharing policy. For now, it allows all request origins.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
        configuration.setAllowedMethods(Collections.singletonList("*"));
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setAllowCredentials(false);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
