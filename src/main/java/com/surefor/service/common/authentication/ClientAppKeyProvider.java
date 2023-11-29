package com.surefor.service.common.authentication;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ClientAppKeyProvider {
    private final String appAuthKey;

    public ClientAppKeyProvider(@Value("${app.auth.key}") final String appAuthKey) {
        this.appAuthKey = appAuthKey;
    }

    public Boolean isValid(@NonNull final String incoming) {
        return incoming.equals(this.appAuthKey);
    }
}
