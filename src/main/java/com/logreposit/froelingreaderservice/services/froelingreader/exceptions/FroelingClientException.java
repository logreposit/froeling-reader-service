package com.logreposit.froelingreaderservice.services.froelingreader.exceptions;

import com.logreposit.froelingreaderservice.exceptions.LogrepositException;

public class FroelingClientException extends LogrepositException
{
    public FroelingClientException(String message)
    {
        super(message);
    }

    public FroelingClientException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
