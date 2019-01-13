package com.logreposit.froelingreaderservice.communication.http.logrepositapi;

import com.logreposit.froelingreaderservice.exceptions.LogrepositException;

public class LogrepositApiClientException extends LogrepositException
{
    public LogrepositApiClientException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
