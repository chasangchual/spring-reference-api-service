package com.surefor.service.common.crypto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Objects;

@Slf4j
/**
  Encodes and verifies the user password. <br>
  Implemented with BCryptPasswordEncoder
  https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/crypto/bcrypt/BCryptPasswordEncoder.html
 */
public class PasswordEncoder {
    private static BCryptPasswordEncoder passwordEncoder = null;
    private static PasswordEncoder instance  = null;

    protected PasswordEncoder() {
        passwordEncoder = new BCryptPasswordEncoder();
    }

    protected String hashPassword(final String password) {
        return passwordEncoder.encode(password);
    }

    protected Boolean isMatched(final String password, final String hashed) {
        return passwordEncoder.matches(password, hashed);
    }

    public static String encode(final String password) {
        ensureInstantiated();
        return instance.hashPassword(password);
    }

    public static Boolean matches(final String password, final String hashed) {
        ensureInstantiated();
        return instance.isMatched(password, hashed);
    }

    protected static void ensureInstantiated() {
        if(Objects.isNull(instance)) {
            instance = new PasswordEncoder();
        }
    }
}
