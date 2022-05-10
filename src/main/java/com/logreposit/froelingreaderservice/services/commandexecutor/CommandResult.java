package com.logreposit.froelingreaderservice.services.commandexecutor;

public record CommandResult(int exitStatus, String stdout, String stderr) {}
