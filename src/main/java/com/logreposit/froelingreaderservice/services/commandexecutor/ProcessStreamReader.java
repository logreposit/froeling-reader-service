package com.logreposit.froelingreaderservice.services.commandexecutor;

import com.logreposit.froelingreaderservice.utils.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ProcessStreamReader
{
    private static final Logger logger = LoggerFactory.getLogger(ProcessStreamReader.class);

    private final InputStream inputStream;

    public ProcessStreamReader(InputStream inputStream)
    {
        this.inputStream = inputStream;
    }

    public String read() throws IOException
    {
        try (InputStreamReader inputStreamReader         = new InputStreamReader(this.inputStream);
             BufferedReader    bufferedInputStreamReader = new BufferedReader(inputStreamReader))
        {
            List<String> lines = new ArrayList<>();
            String       line;

            while ((line = bufferedInputStreamReader.readLine()) != null)
            {
                lines.add(line);
            }

            return String.join("\n", lines);
        }
        catch (IOException e)
        {
            logger.error("Caught IOException while reading InputStream: {}", LoggingUtils.getLogForException(e));

            throw e;
        }
    }
}
