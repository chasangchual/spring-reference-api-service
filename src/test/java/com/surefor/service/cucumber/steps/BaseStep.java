package com.surefor.service.cucumber.steps;

import com.surefor.service.cucumber.common.CucumberScenarioContextManager;
import com.surefor.service.common.exception.DomainEntityNotFoundException;
import com.surefor.service.common.exception.TMSException;
import com.surefor.service.common.exception.TMSPlatformException;
import io.cucumber.java.Scenario;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;

public class BaseStep {
    protected Scenario scenario;
    private final CucumberScenarioContextManager contextManager;

    // TO-DO, delete it after verifying
    // protected final String API_KEY = "eRu9)4u!e>[RG_F>]fV6ux8nL6+C*/t";
    public static final String ACCESS_TOKEN_SUFFIX = "_accessToken";
    public static final String REFRESH_TOKEN_SUFFIX = "_refreshToken";
    public static final String SMS_VERIFICATION_PUBLIC_ID_SUFFIX = "_smsVerificationPublicId";

    public BaseStep(CucumberScenarioContextManager contextManager) {
        this.contextManager = contextManager;
    }

    protected void setUserLoginAccessToken(final String accessToken) {
        contextManager.put(this.scenario.getId() + ACCESS_TOKEN_SUFFIX, accessToken);
    }

    protected String getUserLoginAccessToken() {
        return contextManager.getUserLoginAccessToken(this.scenario.getId() + ACCESS_TOKEN_SUFFIX)
                .orElseThrow(() -> new TMSPlatformException(TMSException.INVALID_TOKEN));
    }

    protected void setUserLoginRefreshToken(final String refreshToken) {
        contextManager.put(this.scenario.getId() + REFRESH_TOKEN_SUFFIX, refreshToken);
    }

    protected String getUserLoginRefreshToken() {
        return contextManager.getUserLoginRefreshToken(this.scenario.getId() + REFRESH_TOKEN_SUFFIX)
                .orElseThrow(() -> new TMSPlatformException(TMSException.INVALID_TOKEN));
    }

    protected void setSmsVerificationPublicId(final UUID verificationPublicId) {
        contextManager.put(this.scenario.getId() + SMS_VERIFICATION_PUBLIC_ID_SUFFIX, verificationPublicId);
    }

    protected UUID getSmsVerificationPublicId() {
        return contextManager.getSmsVerificationPublicId(this.scenario.getId() + SMS_VERIFICATION_PUBLIC_ID_SUFFIX)
                .orElseThrow(() -> new DomainEntityNotFoundException(""));
    }


    protected void put(final String key, final Object value) {
        contextManager.put(prefixScenarioId(key), value);
    }

    protected Optional<Object> get(final String key) {
        return contextManager.get(prefixScenarioId(key));
    }

    protected void remove(final String key) {
        contextManager.remove(prefixScenarioId(key));
    }

    private String prefixScenarioId(final String key) {
        return scenario.getId() + "_" + key;
    }


    protected void checkHttpStatus(Integer httpStatusCode, HttpStatus httpStatus) {
        if (Boolean.FALSE.equals(HttpStatus.valueOf(httpStatusCode).equals(httpStatus))) {
            throw new TMSPlatformException(TMSException.INTERNAL_ERROR);
        }
    }
}
