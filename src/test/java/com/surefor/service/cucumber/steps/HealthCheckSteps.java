package com.surefor.service.cucumber.steps;

import com.surefor.service.cucumber.common.CucumberScenarioContextKeys;
import com.surefor.service.cucumber.common.CucumberScenarioContextManager;
import com.surefor.service.common.http.HttpRequestHelper;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.lang3.tuple.Pair;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class HealthCheckSteps extends BaseStep {
    HttpRequestHelper httpRequestHelper;

    @Autowired
    public HealthCheckSteps(final CucumberScenarioContextManager contextManager,
                            final HttpRequestHelper httpRequestHelper) {
        super(contextManager);

        this.httpRequestHelper = httpRequestHelper;
    }

    @Before
    public void before(Scenario scenario) {
        this.scenario = scenario;
    }

    @When("the consumer makes a health-check request")
    public void theConsumerCreatesEmployeeBelongingToOrganization() {
        Pair<Integer, String> response = httpRequestHelper.get("/ping", String.class);
        put(CucumberScenarioContextKeys.HEALTH_CHECK_RESPONSE, response);
    }

    @Then("the service responds {} status with a message {}")
    public void theServiceRespondsAMessageWithStatus(final Integer statusCode, final String message) {
        Optional<Object> response = get(CucumberScenarioContextKeys.HEALTH_CHECK_RESPONSE);
        assertTrue(response.isPresent());
        assertEquals("pong", ((Pair<Integer, String>) response.get()).getRight());
    }
}