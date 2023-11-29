package com.surefor.service.cucumber.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class CucumberScenarioContextManager {
    private final Map<String, Object> items = new HashMap<>();

    public Optional<UUID> getSmsVerificationPublicId(final String key) {
        UUID smsVerificationPublicId = (UUID) items.get(key);
        return Objects.nonNull(smsVerificationPublicId) ? Optional.of(smsVerificationPublicId) : Optional.empty();
    }

    public Optional<String> getUserLoginAccessToken(final String key) {
        String jwt = (String) items.get(key);
        return Objects.nonNull(jwt) ? Optional.of(jwt) : Optional.empty();
    }

    public Optional<String> getUserLoginRefreshToken(final String key) {
        String jwt = (String) items.get(key);
        return Objects.nonNull(jwt) ? Optional.of(jwt) : Optional.empty();
    }

    public void put(final String key, final Object value) {
        items.put(key, value);
    }

    public Optional<Object> get(final String key) {
        Object value = items.get(key);
        return Objects.nonNull(value) ? Optional.of(value) : Optional.empty();
    }

    public void remove(final String key) {
        Object value = items.get(key);
        if(Objects.isNull(value)) {
            throw new IllegalArgumentException(key);
        }
        items.remove(key);
    }
}
