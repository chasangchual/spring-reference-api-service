package com.surefor.service.cucumber.steps;

import com.surefor.service.api.dto.UserSignupDto;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class UserSignUpSteps extends BaseStep {
    HttpRequestHelper httpRequestHelper;

    @Autowired
    public UserSignUpSteps(final CucumberScenarioContextManager contextManager,
                           final HttpRequestHelper httpRequestHelper) {
        super(contextManager);

        this.httpRequestHelper = httpRequestHelper;
    }

    @Before
    public void before(Scenario scenario) {
        this.scenario = scenario;
    }

    @When("the consumer makes a signup request")
    public void theConsumerMakesASignUpRequest(final List<UserSignupDto.Request> singUpRequest) {
        Map<String, Pair<Integer, StringResponseDto.Response>> responses = new HashMap<>();
        singUpRequest.forEach(request -> {
            Pair<Integer, StringResponseDto.Response> response =  httpRequestHelper.post("/member/v1/signup", request, new ParameterizedTypeReference<StringResponseDto.Response>(){});
            //  Pair<Integer, StringResponseDto> response =  httpRequestHelper.post("/member/v1/signup", request, StringResponseDto.class);
            responses.put(request.getEmail(), response);
        });

        put(CucumberScenarioContextKeys.USER_SIGNUP_REQUEST, responses);
    }

    @Then("the service responds {int} status for {string} signup request")
    public void theServiceRespondsAMessageWithStatusForUserSignup(final Integer statusCode, final String userEmail) {
        Map<String, Pair<Integer, StringResponseDto.Response>> responses = (Map<String, Pair<Integer, StringResponseDto.Response>>) get(CucumberScenarioContextKeys.USER_SIGNUP_REQUEST)
                .orElseThrow(() -> new IllegalArgumentException());

        assertTrue(responses.containsKey(userEmail));
        assertEquals(statusCode, ((Pair<Integer, StringResponseDto.Response>) responses.get(userEmail)).getKey());
    }
}