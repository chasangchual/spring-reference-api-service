package com.surefor.service.cucumber.steps;

import com.surefor.service.cucumber.common.CucumberScenarioContextManager;
import com.surefor.service.common.http.HttpRequestHelper;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommonSteps extends BaseStep {
    HttpRequestHelper httpRequestHelper;

    @Autowired
    public CommonSteps(final CucumberScenarioContextManager contextManager,
                       final HttpRequestHelper httpRequestHelper) {
        super(contextManager);

        this.httpRequestHelper = httpRequestHelper;
    }

    @Before
    public void before(Scenario scenario) {
        this.scenario = scenario;
    }
}