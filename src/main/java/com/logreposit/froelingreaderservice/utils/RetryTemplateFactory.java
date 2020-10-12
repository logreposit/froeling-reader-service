package com.logreposit.froelingreaderservice.utils;

import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RetryTemplateFactory
{
    private RetryTemplateFactory()
    {
    }

    public static RetryTemplate getRetryTemplateWithExponentialBackOffForAllExceptions(int maxAttempts,
                                                                                       long initialBackOffInterval,
                                                                                       double backOffMultiplier)
    {
        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy();
        simpleRetryPolicy.setMaxAttempts(maxAttempts);

        ExponentialBackOffPolicy exponentialBackOffPolicy = new ExponentialBackOffPolicy();
        exponentialBackOffPolicy.setInitialInterval(initialBackOffInterval);
        exponentialBackOffPolicy.setMultiplier(backOffMultiplier);

        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setBackOffPolicy(exponentialBackOffPolicy);
        retryTemplate.setRetryPolicy(simpleRetryPolicy);

        return retryTemplate;
    }
}
