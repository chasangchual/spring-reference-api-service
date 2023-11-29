package com.surefor.service.common.authentication;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Authenticator {
    private static final String BEARER = "Bearer";

    @Value("app.auth.key")
    String authKey;

    public Boolean isValid(final String authorization) {
        if (StringUtils.isNoneBlank(authorization) && authorization.matches(BEARER + "\\s.*")) {
            return getCurrentAuthToken().equalsIgnoreCase(authorization);
        }
        throw new IllegalArgumentException("failed to authorize the request");
    }

    private String getCurrentAuthToken() {
        return String.format("%s %s", BEARER, authKey);
    }
}
