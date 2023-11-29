package com.surefor.service.cucumber.steps;

import com.surefor.service.api.dto.UserLoginDto;
import com.surefor.service.cucumber.common.CucumberScenarioContextKeys;
import com.surefor.service.cucumber.common.CucumberScenarioContextManager;
import com.surefor.service.api.dto.StringResponseDto;
import com.surefor.service.common.http.HttpRequestHelper;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserLoginSteps extends BaseStep {
    HttpRequestHelper httpRequestHelper;

    @Autowired
    public UserLoginSteps(final CucumberScenarioContextManager contextManager,
                          final HttpRequestHelper httpRequestHelper) {
        super(contextManager);

        this.httpRequestHelper = httpRequestHelper;
    }

    @Before
    public void before(Scenario scenario) {
        this.scenario = scenario;
    }

    @When("the consumer makes a login request")
    public void theConsumerMakesALoginRequest(final List<UserLoginDto.Request> loginRequest) {
        Map<String, Pair<Integer, UserLoginDto.Response>> responses = new HashMap<>();
        loginRequest.forEach(request -> {
            Pair<Integer, UserLoginDto.Response> response =  httpRequestHelper.post("/member/v1/authorize", request, new ParameterizedTypeReference<UserLoginDto.Response>(){});
            responses.put(request.getEmail(), response);
        });

        put(CucumberScenarioContextKeys.USER_LOGIN_REQUEST, responses);
    }

    @Then("the service responds {int} status for {string} login request")
    public void theServiceRespondsAMessageWithStatusForUserSignup(final Integer statusCode, final String userEmail) {
        Map<String, Pair<Integer, StringResponseDto.Response>> responses = (Map<String, Pair<Integer, StringResponseDto.Response>>) get(CucumberScenarioContextKeys.USER_LOGIN_REQUEST)
                .orElseThrow(() -> new IllegalArgumentException());

        assertTrue(responses.containsKey(userEmail));
        assertEquals(statusCode, ((Pair<Integer, StringResponseDto.Response>) responses.get(userEmail)).getKey());
    }
}