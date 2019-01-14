package com.logreposit.froelingreaderservice.services.froelingreader.exceptions;

import com.logreposit.froelingreaderservice.exceptions.LogrepositException;

public class FroelingLogrepositServiceException extends LogrepositException
{
    public FroelingLogrepositServiceException()
    {
    }

    public FroelingLogrepositServiceException(String message)
    {
        super(message);
    }

    public FroelingLogrepositServiceException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public FroelingLogrepositServiceException(Throwable cause)
    {
        super(cause);
    }

    public FroelingLogrepositServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
