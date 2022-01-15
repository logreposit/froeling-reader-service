package com.logreposit.froelingreaderservice.metrics.logreposit;

import io.micrometer.core.instrument.config.MeterRegistryConfigValidator;
import io.micrometer.core.instrument.config.validate.Validated;
import io.micrometer.core.instrument.step.StepRegistryConfig;

import static io.micrometer.core.instrument.config.MeterRegistryConfigValidator.checkRequired;
import static io.micrometer.core.instrument.config.validate.PropertyValidator.getString;
import static io.micrometer.core.instrument.config.validate.PropertyValidator.getUrlString;

public interface LogrepositConfig extends StepRegistryConfig
{
    /**
     * Accept configuration defaults
     */
    LogrepositConfig DEFAULT = k -> null;

    @Override
    default String prefix() {
        return "logreposit.metrics";
    }

    default String measurement() {
        return getString(this, "measurement").orElse("app");
    }

    /**
     * @return The URI for the Influx backend. The default is {@code http://localhost:8086}.
     */
    default String uri() {
        return getUrlString(this, "uri").orElse("http://localhost:8086");
    }

    @Override
    default Validated<?> validate() {
        return MeterRegistryConfigValidator.checkAll(this,
                        c -> StepRegistryConfig.validate(c),
                                                     checkRequired("measurement", LogrepositConfig::measurement),
                                                     checkRequired("uri", LogrepositConfig::uri)
        );
    }
}
