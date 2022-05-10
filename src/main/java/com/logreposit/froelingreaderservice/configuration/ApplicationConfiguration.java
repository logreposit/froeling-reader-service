package com.logreposit.froelingreaderservice.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
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
    private String apiBaseUrl;
}
