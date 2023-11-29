package com.surefor.service.common.http;


import com.surefor.service.common.authentication.AuthenticationToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Slf4j
@Component
public class HttpRequestHelper {
    private final RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();

    @Value("${server.port}")
    private int serverPort;

    public String getUrl(String path) {
        return "http://localhost:" + serverPort + path;
    }

    @Value("${app.auth.key}")
    private String apiKey;

    public RestTemplate getRestTemplate(Optional<String> jwt) {
        RestTemplate template = restTemplateBuilder
                .build();

        template.getInterceptors()
                .add((request, body, clientHttpRequestExecution) -> {
                    HttpHeaders headers = request.getHeaders();
                    return clientHttpRequestExecution.execute(request, body);
                });

        template.getInterceptors()
                .add((request, body, clientHttpRequestExecution) -> {
                    HttpHeaders headers = request.getHeaders();
                    // as a default, it adds "X-API-KEY" header in the request during the integration test runs
                    if (Boolean.FALSE.equals(headers.containsKey(AuthenticationToken.API_KEY_HEADER))) {
                        request.getHeaders().add(AuthenticationToken.API_KEY_HEADER, apiKey);
                    }

                    return clientHttpRequestExecution.execute(request, body);
                });

        if (jwt.isPresent()) {
            template.getInterceptors()
                    .add((request, body, clientHttpRequestExecution) -> {
                        HttpHeaders headers = request.getHeaders();
                        if (Boolean.FALSE.equals(headers.containsKey(AuthenticationToken.AUTHORIZATION_HEADER))) {
                            String token = jwt.get().toLowerCase().startsWith("bearer") ? jwt.get() : "Bearer " + jwt.get();
                            request.getHeaders().add(AuthenticationToken.AUTHORIZATION_HEADER, token);
                        }
                        return clientHttpRequestExecution.execute(request, body);
                    });
        }

        return template;
    }

    public <Integer, T> Pair<Integer, T> get(final String jwt, final String path, final Class<T> responseType) {
        return get(Optional.of(jwt), path, responseType);
    }

    public <Integer, T> Pair<Integer, T> get(final String path, final Class<T> responseType) {
        return get(Optional.empty(), path, responseType);
    }

    public <Integer, T> Pair<Integer, T> get(final Optional<String> jwt, final String path, final Class<T> responseType) {
        RestTemplate template = getRestTemplate(jwt);
        try {
            ResponseEntity<T> res = template.exchange(getUrl(path), HttpMethod.GET, null, responseType);
            return new ImmutablePair(res.getStatusCodeValue(), res.getBody());
        } catch (HttpStatusCodeException e) {
            return new ImmutablePair(e.getRawStatusCode(), null);
        }
    }

    public <Integer, T> Pair<Integer, T> get(final String jwt, final String path, final ParameterizedTypeReference<T> responseType) {
        return get(Optional.of(jwt), path, responseType);
    }

    public <Integer, T> Pair<Integer, T> get(final String path, final ParameterizedTypeReference<T> responseType) {
        return get(Optional.empty(), path, responseType);
    }

    public <Integer, T> Pair<Integer, T> get(final Optional<String> jwt, final String path, final ParameterizedTypeReference<T> responseType) {
        RestTemplate template = getRestTemplate(jwt);
        try {
            ResponseEntity<T> res = template.exchange(getUrl(path), HttpMethod.GET, null, responseType);
            return new ImmutablePair(res.getStatusCodeValue(), res.getBody());
        } catch (HttpStatusCodeException e) {
            return new ImmutablePair(e.getRawStatusCode(), null);
        }
    }

    public <Integer, T> Pair<Integer, T> post(final String jwt, final String path, final Object requestBody, final Class<T> responseType) {
        return post(Optional.of(jwt), path, new HttpEntity(requestBody), responseType);
    }

    public <Integer, T> Pair<Integer, T> post(final String path, final Object requestBody, final Class<T> responseType) {
        return post(Optional.empty(), path, new HttpEntity(requestBody), responseType);
    }

    public <Integer, T> Pair<Integer, T> post(final Optional<String> jwt, final String path, final HttpEntity requestEntity, final Class<T> responseType) {
        RestTemplate template = getRestTemplate(jwt);
        try {
            ResponseEntity<T> res = template.exchange(getUrl(path), HttpMethod.POST, requestEntity, responseType);
            return new ImmutablePair(res.getStatusCodeValue(), res.getBody());
        } catch (HttpStatusCodeException e) {
            return new ImmutablePair(e.getRawStatusCode(), e.getResponseBodyAsString());
        }
    }

    public <Integer, T> Pair<Integer, T> post(final String path, final Object requestBody, final ParameterizedTypeReference<T> responseType) {
        return post(Optional.empty(), path, new HttpEntity(requestBody), responseType);
    }

    public <Integer, T> Pair<Integer, T> post(final String jwt, final String path, final Object requestBody, final ParameterizedTypeReference<T> responseType) {
        return post(Optional.of(jwt), path, new HttpEntity(requestBody), responseType);
    }

    public <Integer, T> Pair<Integer, T> post(final Optional<String> jwt, final String path, final Object requestBody, final ParameterizedTypeReference<T> responseType) {
        return post(jwt, path, new HttpEntity(requestBody), responseType);
    }

    public <Integer, T> Pair<Integer, T> post(final Optional<String> jwt, final String path, final HttpEntity requestEntity, final ParameterizedTypeReference<T> responseType) {
        RestTemplate template = getRestTemplate(jwt);
        try {
            ResponseEntity<T> res = template.exchange(getUrl(path), HttpMethod.POST, requestEntity, responseType);
            return new ImmutablePair(res.getStatusCodeValue(), res.getBody());
        } catch (HttpStatusCodeException e) {
            return new ImmutablePair(e.getRawStatusCode(), null);
        }
    }

    public <Integer, T> Pair<Integer, T> put(final String jwt, final String path, final Object requestBody, final Class<T> responseType) {
        return put(Optional.of(jwt), path, new HttpEntity(requestBody), responseType);
    }

    public <Integer, T> Pair<Integer, T> put(final String path, final Object requestBody, final Class<T> responseType) {
        return put(Optional.empty(), path, new HttpEntity(requestBody), responseType);
    }

    public <Integer, T> Pair<Integer, T> put(final Optional<String> jwt, final String path, final Object requestBody, final Class<T> responseType) {
        return put(jwt, path, new HttpEntity(requestBody), responseType);
    }

    public <Integer, T> Pair<Integer, T> put(final Optional<String> jwt, final String path, final HttpEntity requestEntity, final Class<T> responseType) {
        RestTemplate template = getRestTemplate(jwt);
        try {
            ResponseEntity<T> res = template.exchange(getUrl(path), HttpMethod.PUT, requestEntity, responseType);
            return new ImmutablePair(res.getStatusCodeValue(), res.getBody());
        } catch (HttpStatusCodeException e) {
            return new ImmutablePair(e.getRawStatusCode(), null);
        }
    }

    public <Integer, T> Pair<Integer, T> put(final String jwt, final String path, final Object requestBody, final ParameterizedTypeReference<T> responseType) {
        return put(Optional.of(jwt), path, new HttpEntity(requestBody), responseType);
    }

    public <Integer, T> Pair<Integer, T> put(final String path, final Object requestBody, final ParameterizedTypeReference<T> responseType) {
        return put(Optional.empty(), path, new HttpEntity(requestBody), responseType);
    }

    public <Integer, T> Pair<Integer, T> put(final Optional<String> jwt, final String path, final Object requestBody, final ParameterizedTypeReference<T> responseType) {
        return put(jwt, path, new HttpEntity(requestBody), responseType);
    }

    public <Integer, T> Pair<Integer, T> put(Optional<String> jwt, final String path, final HttpEntity requestEntity, final ParameterizedTypeReference<T> responseType) {
        RestTemplate template = getRestTemplate(jwt);
        try {
            ResponseEntity<T> res = template.exchange(getUrl(path), HttpMethod.PUT, requestEntity, responseType);
            return new ImmutablePair(res.getStatusCodeValue(), res.getBody());
        } catch (HttpStatusCodeException e) {
            return new ImmutablePair(e.getRawStatusCode(), null);
        }
    }

    public <Integer, T> Pair<Integer, T> post(final String path, final LinkedMultiValueMap<String, Object> requestBody, final Class<T> responseType, final MediaType mediaType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        return post(Optional.empty(), path, new HttpEntity(requestBody, headers), responseType);
    }

    public <Integer, T> Pair<Integer, T> post(final String jwt, final String path, final LinkedMultiValueMap<String, Object> requestBody, final Class<T> responseType, final MediaType mediaType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        return post(Optional.of(jwt), path, new HttpEntity(requestBody, headers), responseType);
    }

    public <Integer, T> Pair<Integer, T> post(final Optional<String> jwt, final String path, final LinkedMultiValueMap<String, Object> requestBody, final Class<T> responseType, final MediaType mediaType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        return post(jwt, path, new HttpEntity(requestBody, headers), responseType);
    }

    public <Integer, T> Pair<Integer, T> delete(final String path, final Object requestBody, final Class<T> responseType) {
        return delete(Optional.empty(), path, new HttpEntity(requestBody), responseType);
    }

    public <Integer, T> Pair<Integer, T> delete(final String jwt, final String path, final Object requestBody, final Class<T> responseType) {
        return delete(Optional.of(jwt), path, new HttpEntity(requestBody), responseType);
    }

    public <Integer, T> Pair<Integer, T> delete(final Optional<String> jwt, final String path, final HttpEntity requestBody, final Class<T> responseType) {
        RestTemplate template = getRestTemplate(jwt);
        try {
            ResponseEntity<T> res = template.exchange(getUrl(path), HttpMethod.DELETE, requestBody, responseType);
            return new ImmutablePair(res.getStatusCodeValue(), res.getBody());
        } catch (HttpStatusCodeException e) {
            return new ImmutablePair(e.getRawStatusCode(), e.getResponseBodyAsString());
        }
    }
}