package com.logreposit.froelingreaderservice.services.froelingreader;

import com.logreposit.froelingreaderservice.services.commandexecutor.CommandExecutor;
import com.logreposit.froelingreaderservice.services.commandexecutor.CommandExecutorException;
import com.logreposit.froelingreaderservice.services.commandexecutor.CommandResult;
import com.logreposit.froelingreaderservice.services.froelingreader.exceptions.FroelingClientException;
import com.logreposit.froelingreaderservice.services.froelingreader.exceptions.FroelingOutputParserException;
import com.logreposit.froelingreaderservice.services.froelingreader.models.FroelingError;
import com.logreposit.froelingreaderservice.services.froelingreader.models.FroelingState;
import com.logreposit.froelingreaderservice.services.froelingreader.models.FroelingValueAddress;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@RunWith(SpringRunner.class)
public class FroelingClientImplTests
{
    private FroelingClient froelingClient;

    @MockBean
    private CommandExecutor commandExecutor;

    @MockBean
    private FroelingOutputParser froelingOutputParser;

    @Captor
    private ArgumentCaptor<List<String>> commandArgumentCaptor;

    @Before
    public void setUp()
    {
        this.froelingClient = new FroelingClientImpl(this.commandExecutor, this.froelingOutputParser);
    }

    @Test
    public void testGetState() throws FroelingClientException, CommandExecutorException, FroelingOutputParserException
    {
        String        stdout        = UUID.randomUUID().toString();
        CommandResult commandResult = new CommandResult(0, stdout, "");

        Mockito.when(this.commandExecutor.execute(Mockito.anyList())).thenReturn(commandResult);

        FroelingState froelingState = new FroelingState("version", new Date(), new HashMap<>());

        Mockito.when(this.froelingOutputParser.parseState(Mockito.same(stdout))).thenReturn(froelingState);

        FroelingState retrievedState = this.froelingClient.getState();

        Assert.assertSame(froelingState, retrievedState);

        Mockito.verify(this.commandExecutor, Mockito.times(1)).execute(this.commandArgumentCaptor.capture());

        List<String> capturedCommandParts = this.commandArgumentCaptor.getValue();

        Assert.assertNotNull(capturedCommandParts);
        Assert.assertFalse(capturedCommandParts.isEmpty());
        Assert.assertEquals(2, capturedCommandParts.size());
        Assert.assertEquals("p4", capturedCommandParts.get(0));
        Assert.assertEquals("state", capturedCommandParts.get(1));

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(this.froelingOutputParser, Mockito.times(1)).parseState(stringArgumentCaptor.capture());

        String capturedStdout = stringArgumentCaptor.getValue();

        Assert.assertNotNull(capturedStdout);
        Assert.assertSame(stdout, capturedStdout);
    }

    @Test
    public void testGetErrors() throws FroelingClientException, CommandExecutorException, FroelingOutputParserException
    {
        String        stdout        = UUID.randomUUID().toString();
        CommandResult commandResult = new CommandResult(0, stdout, "");

        Mockito.when(this.commandExecutor.execute(Mockito.anyList())).thenReturn(commandResult);

        List<FroelingError> froelingErrors = new ArrayList<>();

        Mockito.when(this.froelingOutputParser.parseErrors(Mockito.same(stdout))).thenReturn(froelingErrors);

        List<FroelingError> retrievedErrors = this.froelingClient.getErrors();

        Assert.assertSame(froelingErrors, retrievedErrors);

        Mockito.verify(this.commandExecutor, Mockito.times(1)).execute(this.commandArgumentCaptor.capture());

        List<String> capturedCommandParts = this.commandArgumentCaptor.getValue();

        Assert.assertNotNull(capturedCommandParts);
        Assert.assertFalse(capturedCommandParts.isEmpty());
        Assert.assertEquals(2, capturedCommandParts.size());
        Assert.assertEquals("p4", capturedCommandParts.get(0));
        Assert.assertEquals("errors", capturedCommandParts.get(1));

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(this.froelingOutputParser, Mockito.times(1)).parseErrors(stringArgumentCaptor.capture());

        String capturedStdout = stringArgumentCaptor.getValue();

        Assert.assertNotNull(capturedStdout);
        Assert.assertSame(stdout, capturedStdout);
    }

    @Test
    public void testGetValueAddresses() throws FroelingClientException, CommandExecutorException, FroelingOutputParserException
    {
        String        stdout        = UUID.randomUUID().toString();
        CommandResult commandResult = new CommandResult(0, stdout, "");

        Mockito.when(this.commandExecutor.execute(Mockito.anyList())).thenReturn(commandResult);

        List<FroelingValueAddress> froelingValueAddresses = new ArrayList<>();

        Mockito.when(this.froelingOutputParser.parseValueAddresses(Mockito.same(stdout))).thenReturn(froelingValueAddresses);

        List<FroelingValueAddress> retrievedValueAddresses = this.froelingClient.getValueAddresses();

        Assert.assertSame(froelingValueAddresses, retrievedValueAddresses);

        Mockito.verify(this.commandExecutor, Mockito.times(1)).execute(this.commandArgumentCaptor.capture());

        List<String> capturedCommandParts = this.commandArgumentCaptor.getValue();

        Assert.assertNotNull(capturedCommandParts);
        Assert.assertFalse(capturedCommandParts.isEmpty());
        Assert.assertEquals(2, capturedCommandParts.size());
        Assert.assertEquals("p4", capturedCommandParts.get(0));
        Assert.assertEquals("values", capturedCommandParts.get(1));

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(this.froelingOutputParser, Mockito.times(1)).parseValueAddresses(stringArgumentCaptor.capture());

        String capturedStdout = stringArgumentCaptor.getValue();

        Assert.assertNotNull(capturedStdout);
        Assert.assertSame(stdout, capturedStdout);
    }

    @Test
    public void testGetValue() throws FroelingClientException, CommandExecutorException, FroelingOutputParserException
    {
        String        stdout        = UUID.randomUUID().toString();
        CommandResult commandResult = new CommandResult(0, stdout, "");

        Mockito.when(this.commandExecutor.execute(Mockito.anyList())).thenReturn(commandResult);

        int value = 123;

        Mockito.when(this.froelingOutputParser.parseValue(Mockito.same(stdout))).thenReturn(value);

        int retrievedValue = this.froelingClient.getValue("0x000", 1);

        Assert.assertSame(value, retrievedValue);

        Mockito.verify(this.commandExecutor, Mockito.times(1)).execute(this.commandArgumentCaptor.capture());

        List<String> capturedCommandParts = this.commandArgumentCaptor.getValue();

        Assert.assertNotNull(capturedCommandParts);
        Assert.assertFalse(capturedCommandParts.isEmpty());
        Assert.assertEquals(4, capturedCommandParts.size());
        Assert.assertEquals("p4", capturedCommandParts.get(0));
        Assert.assertEquals("getv", capturedCommandParts.get(1));
        Assert.assertEquals("-a", capturedCommandParts.get(2));
        Assert.assertEquals("0x000", capturedCommandParts.get(3));

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(this.froelingOutputParser, Mockito.times(1)).parseValue(stringArgumentCaptor.capture());

        String capturedStdout = stringArgumentCaptor.getValue();

        Assert.assertNotNull(capturedStdout);
        Assert.assertSame(stdout, capturedStdout);
    }

    @Test
    public void testGetValue_Multiplier() throws FroelingClientException, CommandExecutorException, FroelingOutputParserException
    {
        String        stdout        = UUID.randomUUID().toString();
        CommandResult commandResult = new CommandResult(0, stdout, "");

        Mockito.when(this.commandExecutor.execute(Mockito.anyList())).thenReturn(commandResult);

        int value = 123;

        Mockito.when(this.froelingOutputParser.parseValue(Mockito.same(stdout))).thenReturn(value);

        int retrievedValue = this.froelingClient.getValue("0x000", 2);

        Assert.assertEquals(value * 2, retrievedValue);

        Mockito.verify(this.commandExecutor, Mockito.times(1)).execute(this.commandArgumentCaptor.capture());

        List<String> capturedCommandParts = this.commandArgumentCaptor.getValue();

        Assert.assertNotNull(capturedCommandParts);
        Assert.assertFalse(capturedCommandParts.isEmpty());
        Assert.assertEquals(4, capturedCommandParts.size());
        Assert.assertEquals("p4", capturedCommandParts.get(0));
        Assert.assertEquals("getv", capturedCommandParts.get(1));
        Assert.assertEquals("-a", capturedCommandParts.get(2));
        Assert.assertEquals("0x000", capturedCommandParts.get(3));

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(this.froelingOutputParser, Mockito.times(1)).parseValue(stringArgumentCaptor.capture());

        String capturedStdout = stringArgumentCaptor.getValue();

        Assert.assertNotNull(capturedStdout);
        Assert.assertSame(stdout, capturedStdout);
    }
}
