package com.logreposit.froelingreaderservice.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class LoggingUtils
{
    private static final String DEFAULT = "<NOT_SERIALIZABLE>";

    private LoggingUtils()
    {
    }

    public static String serialize(Object object)
    {
        ObjectMapper objectMapper = createObjectMapper();

        try
        {
            return objectMapper.writeValueAsString(object);
        }
        catch (JsonProcessingException e)
        {
            return DEFAULT;
        }
    }

    public static String getLogForException(Throwable exception)
    {
        String cls        = exception.getClass().getName();
        String message    = exception.getMessage();
        String stackTrace = ExceptionUtils.getStackTrace(exception);

        String logLine = String.format("[%s] %s%n%s", cls, message, stackTrace);

        return logLine;
    }

    private static ObjectMapper createObjectMapper()
    {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        return objectMapper;
    }
}
