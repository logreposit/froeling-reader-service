package com.logreposit.froelingreaderservice.services.froelingreader.exceptions;

import com.logreposit.froelingreaderservice.exceptions.LogrepositException;

public class FroelingClientException extends LogrepositException
{
    public FroelingClientException()
    {
    }

    public FroelingClientException(String message)
    {
        super(message);
    }

    public FroelingClientException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public FroelingClientException(Throwable cause)
    {
        super(cause);
    }

    public FroelingClientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
