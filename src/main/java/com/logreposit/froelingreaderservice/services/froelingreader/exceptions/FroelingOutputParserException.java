package com.logreposit.froelingreaderservice.services.froelingreader.exceptions;

import com.logreposit.froelingreaderservice.exceptions.LogrepositException;

public class FroelingOutputParserException extends LogrepositException
{
    public FroelingOutputParserException()
    {
    }

    public FroelingOutputParserException(String message)
    {
        super(message);
    }

    public FroelingOutputParserException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public FroelingOutputParserException(Throwable cause)
    {
        super(cause);
    }

    public FroelingOutputParserException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
