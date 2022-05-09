package com.logreposit.froelingreaderservice.services.logreposit;

import com.logreposit.froelingreaderservice.configuration.ApplicationConfiguration;
import com.logreposit.froelingreaderservice.services.logreposit.dtos.ingress.data.IngressData;
import com.logreposit.froelingreaderservice.services.logreposit.dtos.ingress.definition.IngressDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Service
public class LogrepositApiService
{
    private static final Logger logger = LoggerFactory.getLogger(LogrepositApiService.class);

    private final ApplicationConfiguration applicationConfiguration;
    private final RestTemplate             restTemplate;
    private final LogrepositIngressDefinitionProvider ingressDefinitionProvider;

    public LogrepositApiService(RestTemplateBuilder restTemplateBuilder, ApplicationConfiguration applicationConfiguration, LogrepositIngressDefinitionProvider ingressDefinitionProvider) {
        this.applicationConfiguration = applicationConfiguration;
        this.ingressDefinitionProvider = ingressDefinitionProvider;

        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.of(10, ChronoUnit.SECONDS))
                .setReadTimeout(Duration.of(10, ChronoUnit.SECONDS))
                .build();
    }

    @Retryable(
            value = {RestClientException.class},
            exclude = {HttpClientErrorException.UnprocessableEntity.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 500)
    )
    public void pushData(IngressData data) {
        final var url = applicationConfiguration.getApiBaseUrl() + "/v2/ingress/data";

        logger.info("Sending data to Logreposit API ({}): {}", url, data);

        final var response = restTemplate.postForObject(url, httpEntity(data), String.class);

        logger.info("Response from Logreposit API: {}", response);
    }

    @Recover
    void recoverUnprocessableEntity(HttpClientErrorException.UnprocessableEntity e, IngressData data) {
        logger.warn("Error while sending data to Logreposit API. Got unprocessable entity. Most likely a device definition validation error. {}", data, e);
        logger.warn("Updating device ingress definition ...");

        final var url = applicationConfiguration.getApiBaseUrl() + "/v2/ingress/definition";

        restTemplate.put(url, httpEntity(ingressDefinitionProvider.getIngressDefinition()));
    }

    @Recover
    void recoverThrowable(Throwable e, IngressData data) throws Throwable
    {
        logger.error("Could not send data to Logreposit API: {}", data, e);

        throw e;
    }

    private HttpEntity<IngressDefinition> httpEntity(IngressDefinition definition) {
        return new HttpEntity<>(definition, createHeaders());
    }

    private HttpEntity<IngressData> httpEntity(IngressData data) {
        return new HttpEntity<>(data, createHeaders());
    }

    private HttpHeaders createHeaders() {
        final var deviceToken = applicationConfiguration.getDeviceToken();
        final var httpHeaders = new HttpHeaders();

        httpHeaders.add("x-device-token", deviceToken);

        return httpHeaders;
    }
}
