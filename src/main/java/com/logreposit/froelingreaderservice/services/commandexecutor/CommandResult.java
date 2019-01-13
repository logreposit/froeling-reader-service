package com.logreposit.froelingreaderservice.services.commandexecutor;

public class CommandResult
{
    private final int    exitStatus;
    private final String stdout;
    private final String stderr;

    public CommandResult(int exitStatus, String stdout, String stderr)
    {
        this.exitStatus = exitStatus;
        this.stdout     = stdout;
        this.stderr     = stderr;
    }

    public int getExitStatus()
    {
        return this.exitStatus;
    }

    public String getStdout()
    {
        return this.stdout;
    }

    public String getStderr()
    {
        return this.stderr;
    }
}
