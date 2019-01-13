package com.logreposit.froelingreaderservice.services.commandexecutor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
public class CommandExecutorImplTests
{
    private CommandExecutorImpl commandExecutor;

    @Before
    public void setUp()
    {
        this.commandExecutor = new CommandExecutorImpl();
    }

    @Test
    public void testExecute_ls() throws Exception
    {
        List<String> commandParts = Arrays.asList("ls", "/");

        CommandResult commandResult = this.commandExecutor.execute(commandParts);

        Assert.assertNotNull(commandResult);
        Assert.assertEquals(0, commandResult.getExitStatus());
        Assert.assertTrue(StringUtils.isEmpty(commandResult.getStderr()));
        Assert.assertFalse(StringUtils.isEmpty(commandResult.getStdout()));
    }
}
