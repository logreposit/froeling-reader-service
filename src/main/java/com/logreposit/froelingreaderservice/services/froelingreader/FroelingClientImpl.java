package com.logreposit.froelingreaderservice.services.froelingreader;

import com.logreposit.froelingreaderservice.services.commandexecutor.CommandExecutor;
import com.logreposit.froelingreaderservice.services.commandexecutor.CommandExecutorException;
import com.logreposit.froelingreaderservice.services.commandexecutor.CommandResult;
import com.logreposit.froelingreaderservice.services.froelingreader.exceptions.FroelingClientException;
import com.logreposit.froelingreaderservice.services.froelingreader.exceptions.FroelingOutputParserException;
import com.logreposit.froelingreaderservice.services.froelingreader.models.FroelingError;
import com.logreposit.froelingreaderservice.services.froelingreader.models.FroelingState;
import com.logreposit.froelingreaderservice.services.froelingreader.models.FroelingValueAddress;
import com.logreposit.froelingreaderservice.utils.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

@Component
public class FroelingClientImpl implements FroelingClient
{
    private static final Logger logger = LoggerFactory.getLogger(FroelingClientImpl.class);

    private final CommandExecutor      commandExecutor;
    private final FroelingOutputParser froelingOutputParser;

    public FroelingClientImpl(CommandExecutor      commandExecutor,
                              FroelingOutputParser froelingOutputParser)
    {
        this.commandExecutor      = commandExecutor;
        this.froelingOutputParser = froelingOutputParser;
    }

    @Override
    public FroelingState getState() throws FroelingClientException
    {
        List<String>  commandParts = Arrays.asList("p4", "state");
        String        stdout       = this.executeCommand(commandParts);

        try
        {
            FroelingState froelingState = this.froelingOutputParser.parseState(stdout);

            return froelingState;
        }
        catch (FroelingOutputParserException e)
        {
            logger.error("Unable to convert p4 output to FroelingState: {}", LoggingUtils.getLogForException(e));

            throw new FroelingClientException("Unable to convert p4 output to FroelingState", e);
        }
    }

    @Override
    public List<FroelingError> getErrors() throws FroelingClientException
    {
        List<String> commandParts = Arrays.asList("p4", "errors");
        String       stdout       = this.executeCommand(commandParts);

        try
        {
            List<FroelingError> froelingErrors = this.froelingOutputParser.parseErrors(stdout);

            return froelingErrors;
        }
        catch (FroelingOutputParserException e)
        {
            logger.error("Unable to convert p4 output to List<FroelingError>: {}", LoggingUtils.getLogForException(e));

            throw new FroelingClientException("Unable to convert p4 output to List<FroelingError>", e);
        }
    }

    @Override
    public List<FroelingValueAddress> getValueAddresses() throws FroelingClientException
    {
        List<String> commandParts = Arrays.asList("p4", "values");
        String       stdout       = this.executeCommand(commandParts);

        try
        {
            List<FroelingValueAddress> froelingErrors = this.froelingOutputParser.parseValueAddresses(stdout);

            return froelingErrors;
        }
        catch (FroelingOutputParserException e)
        {
            logger.error("Unable to convert p4 output to List<FroelingValueAddress>: {}", LoggingUtils.getLogForException(e));

            throw new FroelingClientException("Unable to convert p4 output to List<FroelingValueAddress>", e);
        }
    }

    @Override
    public int getValue(String address, Integer multiplier) throws FroelingClientException
    {
        List<String> commandParts = Arrays.asList("p4", "getv", "-a", address);
        String       stdout       = this.executeCommand(commandParts);

        try
        {
            int retrievedValue = this.froelingOutputParser.parseValue(stdout);
            int value          = 0;

            if (multiplier != null && multiplier != 0)
            {
                value = retrievedValue / multiplier;
            }

            logger.info("Address {} -> value / multiplier -> {} / {} => {}", address, retrievedValue, multiplier, value);

            return value;
        }
        catch (FroelingOutputParserException e)
        {
            logger.error("Unable to convert p4 output to int: {}", LoggingUtils.getLogForException(e));

            throw new FroelingClientException("Unable to convert p4 output to int", e);
        }
    }

    private String executeCommand(List<String> commandParts) throws FroelingClientException
    {
        try
        {
            CommandResult commandResult = this.commandExecutor.execute(commandParts);

            if (commandResult == null)
            {
                logger.error("Error executing command. CommandResult is null.");

                throw new FroelingClientException("Error executing command. CommandResult is null.");
            }

            if (commandResult.exitStatus() != 0)
            {
                logger.error("Command {} exited with exit status {}, stderr: {}, stdout: {}",
                        String.join(" ", commandParts),
                        commandResult.exitStatus(),
                        commandResult.stderr(),
                        commandResult.stdout()
                );

                throw new FroelingClientException("Command exited with status != 0");
            }

            if (StringUtils.isEmpty(commandResult.stdout()))
            {
                logger.error("Command {} returned empty stdout.", String.join(" ", commandParts));

                throw new FroelingClientException("Command returned empty stdout.");
            }

            return commandResult.stdout();
        }
        catch (CommandExecutorException e)
        {
            logger.error("Caught CommandExecutorException while executing command {}: {}", String.join(" ", commandParts), LoggingUtils.getLogForException(e));

            throw new FroelingClientException("Caught CommandExecutorException while executing command", e);
        }
    }
}
