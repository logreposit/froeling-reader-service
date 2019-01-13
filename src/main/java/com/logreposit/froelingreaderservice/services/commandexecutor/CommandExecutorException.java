package com.logreposit.froelingreaderservice.services.commandexecutor;

import com.logreposit.froelingreaderservice.exceptions.LogrepositException;

public class CommandExecutorException extends LogrepositException
{
    public CommandExecutorException(String message)
    {
        super(message);
    }

    public CommandExecutorException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
