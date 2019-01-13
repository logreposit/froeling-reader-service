package com.logreposit.froelingreaderservice.services.commandexecutor;

import com.logreposit.froelingreaderservice.utils.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Component
public class CommandExecutorImpl implements CommandExecutor
{
    private static final Logger logger = LoggerFactory.getLogger(CommandExecutorImpl.class);

    @Override
    public CommandResult execute(List<String> commandParts) throws CommandExecutorException
    {
        if (CollectionUtils.isEmpty(commandParts))
        {
            logger.error("CommandParts cannot be empty.");

            throw new CommandExecutorException("CommandParts cannot be empty.");
        }

        ProcessBuilder processBuilder = new ProcessBuilder(commandParts);

        try
        {
            Process process = processBuilder.start();

            InputStream inputStream = process.getInputStream();
            InputStream errorStream = process.getErrorStream();

            Future<String> inputStreamCompletableFuture = readInputStreamAsync(inputStream);
            Future<String> errorStreamCompletableFuture = readInputStreamAsync(errorStream);

            int    exitStatus = process.waitFor();
            String stdout     = inputStreamCompletableFuture.get();
            String stderr     = errorStreamCompletableFuture.get();

            return new CommandResult(exitStatus, stdout, stderr);
        }
        catch (IOException e)
        {
            logger.error("Caught IOException: {}", LoggingUtils.getLogForException(e));

            throw new CommandExecutorException("Caught IOException", e);
        }
        catch (InterruptedException e)
        {
            logger.error("Caught InterruptedException: {}", LoggingUtils.getLogForException(e));

            throw new CommandExecutorException("Caught InterruptedException", e);
        }
        catch (ExecutionException e)
        {
            logger.error("Caught ExecutionException while executing command. Cause: {}", LoggingUtils.getLogForException(e.getCause()));

            throw new CommandExecutorException("Caught ExecutionException while executing command", e.getCause());
        }
    }

    private static Future<String> readInputStreamAsync(InputStream inputStream)
    {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();

        Executors.newSingleThreadExecutor().submit(() -> {
            ProcessStreamReader processStreamReader = new ProcessStreamReader(inputStream);

            try
            {
                String result = processStreamReader.read();

                completableFuture.complete(result);
            }
            catch (IOException e)
            {
                completableFuture.completeExceptionally(e);
            }
        });

        return completableFuture;
    }
}
