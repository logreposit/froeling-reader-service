package com.logreposit.froelingreaderservice.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Validated
@Configuration
@ConfigurationProperties(value = "froelingreaderservice")
public class ApplicationConfiguration
{
    @NotBlank
    private String deviceToken;

    @NotNull
    private Long collectInterval;

    @NotBlank
    private String deviceTimezone;

    @NotBlank
    private String apiBaseUrl;

    @NotNull
    private Integer apiClientRetryCount;

    @NotNull
    private Long apiClientRetryInitialBackOffInterval;

    @NotNull
    private Double apiClientRetryBackOffMultiplier;

    public String getDeviceToken()
    {
        return this.deviceToken;
    }

    public void setDeviceToken(String deviceToken)
    {
        this.deviceToken = deviceToken;
    }

    public Long getCollectInterval()
    {
        return this.collectInterval;
    }

    public void setCollectInterval(Long collectInterval)
    {
        this.collectInterval = collectInterval;
    }

    public String getDeviceTimezone()
    {
        return this.deviceTimezone;
    }

    public void setDeviceTimezone(String deviceTimezone)
    {
        this.deviceTimezone = deviceTimezone;
    }

    public String getApiBaseUrl()
    {
        return this.apiBaseUrl;
    }

    public void setApiBaseUrl(String apiBaseUrl)
    {
        this.apiBaseUrl = apiBaseUrl;
    }

    public Integer getApiClientRetryCount()
    {
        return this.apiClientRetryCount;
    }

    public void setApiClientRetryCount(Integer apiClientRetryCount)
    {
        this.apiClientRetryCount = apiClientRetryCount;
    }

    public Long getApiClientRetryInitialBackOffInterval()
    {
        return this.apiClientRetryInitialBackOffInterval;
    }

    public void setApiClientRetryInitialBackOffInterval(Long apiClientRetryInitialBackOffInterval)
    {
        this.apiClientRetryInitialBackOffInterval = apiClientRetryInitialBackOffInterval;
    }

    public Double getApiClientRetryBackOffMultiplier()
    {
        return this.apiClientRetryBackOffMultiplier;
    }

    public void setApiClientRetryBackOffMultiplier(Double apiClientRetryBackOffMultiplier)
    {
        this.apiClientRetryBackOffMultiplier = apiClientRetryBackOffMultiplier;
    }
}
