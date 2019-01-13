package com.logreposit.froelingreaderservice.exceptions;

public class LogrepositException extends Exception
{
    public LogrepositException()
    {
    }

    public LogrepositException(String message)
    {
        super(message);
    }

    public LogrepositException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public LogrepositException(Throwable cause)
    {
        super(cause);
    }

    public LogrepositException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
