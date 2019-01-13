package com.logreposit.froelingreaderservice.services.commandexecutor;

import java.util.List;

public interface CommandExecutor
{
    CommandResult execute(List<String> commandParts) throws CommandExecutorException;
}
