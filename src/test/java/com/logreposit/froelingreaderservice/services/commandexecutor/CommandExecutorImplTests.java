package com.logreposit.froelingreaderservice.services.commandexecutor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CommandExecutorImplTests
{
    private CommandExecutorImpl commandExecutor;

    @BeforeEach
    public void setUp()
    {
        this.commandExecutor = new CommandExecutorImpl();
    }

    @Test
    public void testExecute_ls() throws Exception
    {
        List<String> commandParts = Arrays.asList("ls", "/");

        CommandResult commandResult = this.commandExecutor.execute(commandParts);

        assertThat(commandResult).isNotNull();
        assertThat(commandResult.exitStatus()).isEqualTo(0L);
        assertThat(commandResult.stderr()).isBlank();
        assertThat(commandResult.stdout()).isNotBlank();
    }
}
