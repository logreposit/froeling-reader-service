package com.logreposit.froelingreaderservice.services.froelingreader;

import com.logreposit.froelingreaderservice.services.commandexecutor.CommandExecutor;
import com.logreposit.froelingreaderservice.services.commandexecutor.CommandExecutorException;
import com.logreposit.froelingreaderservice.services.commandexecutor.CommandResult;
import com.logreposit.froelingreaderservice.services.froelingreader.exceptions.FroelingClientException;
import com.logreposit.froelingreaderservice.services.froelingreader.exceptions.FroelingOutputParserException;
import com.logreposit.froelingreaderservice.services.froelingreader.models.FroelingError;
import com.logreposit.froelingreaderservice.services.froelingreader.models.FroelingState;
import com.logreposit.froelingreaderservice.services.froelingreader.models.FroelingValueAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class FroelingClientImplTests
{
    private FroelingClient froelingClient;

    @MockBean
    private CommandExecutor commandExecutor;

    @MockBean
    private FroelingOutputParser froelingOutputParser;

    @Captor
    private ArgumentCaptor<List<String>> commandArgumentCaptor;

    @BeforeEach
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

        assertThat(retrievedState).isSameAs(froelingState);

        Mockito.verify(this.commandExecutor, Mockito.times(1)).execute(this.commandArgumentCaptor.capture());

        List<String> capturedCommandParts = this.commandArgumentCaptor.getValue();

        assertThat(capturedCommandParts).hasSize(2);
        assertThat(capturedCommandParts.get(0)).isEqualTo("p4");
        assertThat(capturedCommandParts.get(1)).isEqualTo("state");

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(this.froelingOutputParser, Mockito.times(1)).parseState(stringArgumentCaptor.capture());

        String capturedStdout = stringArgumentCaptor.getValue();

        assertThat(capturedStdout).isNotNull();
        assertThat(capturedStdout).isSameAs(stdout);
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

        assertThat(retrievedErrors).isSameAs(froelingErrors);

        Mockito.verify(this.commandExecutor, Mockito.times(1)).execute(this.commandArgumentCaptor.capture());

        List<String> capturedCommandParts = this.commandArgumentCaptor.getValue();

        assertThat(capturedCommandParts).hasSize(2);
        assertThat(capturedCommandParts.get(0)).isEqualTo("p4");
        assertThat(capturedCommandParts.get(0)).isEqualTo("errors");

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(this.froelingOutputParser, Mockito.times(1)).parseErrors(stringArgumentCaptor.capture());

        String capturedStdout = stringArgumentCaptor.getValue();

        assertThat(capturedStdout).isNotNull();
        assertThat(capturedStdout).isEqualTo(stdout);
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

        assertThat(retrievedValueAddresses).isSameAs(froelingValueAddresses);

        Mockito.verify(this.commandExecutor, Mockito.times(1)).execute(this.commandArgumentCaptor.capture());

        List<String> capturedCommandParts = this.commandArgumentCaptor.getValue();

        assertThat(capturedCommandParts).hasSize(2);
        assertThat(capturedCommandParts.get(0)).isEqualTo("p4");
        assertThat(capturedCommandParts.get(0)).isEqualTo("values");

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(this.froelingOutputParser, Mockito.times(1)).parseValueAddresses(stringArgumentCaptor.capture());

        String capturedStdout = stringArgumentCaptor.getValue();

        assertThat(capturedStdout).isNotNull();
        assertThat(capturedStdout).isSameAs(stdout);
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

        assertThat(retrievedValue).isSameAs(value);

        Mockito.verify(this.commandExecutor, Mockito.times(1)).execute(this.commandArgumentCaptor.capture());

        List<String> capturedCommandParts = this.commandArgumentCaptor.getValue();

        assertThat(capturedCommandParts).hasSize(4);
        assertThat(capturedCommandParts.get(0)).isEqualTo("p4");
        assertThat(capturedCommandParts.get(1)).isEqualTo("getv");
        assertThat(capturedCommandParts.get(2)).isEqualTo("-a");
        assertThat(capturedCommandParts.get(3)).isEqualTo("0x000");

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(this.froelingOutputParser, Mockito.times(1)).parseValue(stringArgumentCaptor.capture());

        String capturedStdout = stringArgumentCaptor.getValue();

        assertThat(capturedStdout).isNotNull();
        assertThat(capturedStdout).isSameAs(stdout);
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

        assertThat(retrievedValue).isEqualTo(value / 2);

        Mockito.verify(this.commandExecutor, Mockito.times(1)).execute(this.commandArgumentCaptor.capture());

        List<String> capturedCommandParts = this.commandArgumentCaptor.getValue();

        assertThat(capturedCommandParts).hasSize(4);
        assertThat(capturedCommandParts.get(0)).isEqualTo("p4");
        assertThat(capturedCommandParts.get(1)).isEqualTo("getv");
        assertThat(capturedCommandParts.get(2)).isEqualTo("-a");
        assertThat(capturedCommandParts.get(3)).isEqualTo("0x000");

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(this.froelingOutputParser, Mockito.times(1)).parseValue(stringArgumentCaptor.capture());

        String capturedStdout = stringArgumentCaptor.getValue();

        assertThat(capturedStdout).isNotNull();
        assertThat(capturedStdout).isSameAs(stdout);
    }
}
