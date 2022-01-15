package com.logreposit.froelingreaderservice.configuration;

import com.logreposit.froelingreaderservice.metrics.logreposit.LogrepositConfig;
import com.logreposit.froelingreaderservice.metrics.logreposit.LogrepositMeterRegistry;
import io.micrometer.core.instrument.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;

@Configuration
public class MetricsConfiguration
{
    @Bean
    public LogrepositConfig logrepositConfig() {
        return LogrepositConfig.DEFAULT;
    }

    @Bean
    public LogrepositMeterRegistry logrepositMeterRegistry(LogrepositConfig logrepositConfig, Clock clock) {
        var threadFactory = Executors.defaultThreadFactory();

        return new LogrepositMeterRegistry(logrepositConfig, clock, threadFactory, null);
    }
}
