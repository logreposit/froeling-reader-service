package com.logreposit.froelingreaderservice.services.froelingreader;

import com.logreposit.froelingreaderservice.services.froelingreader.models.FroelingS3200LogData;
import com.logreposit.froelingreaderservice.utils.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class FroelingLogrepositServiceImpl implements FroelingLogrepositService
{
    private static final Logger logger = LoggerFactory.getLogger(FroelingLogrepositServiceImpl.class);

    private final FroelingReader froelingReader;

    public FroelingLogrepositServiceImpl(FroelingReader froelingReader)
    {
        this.froelingReader = froelingReader;
    }

    @Override
    @Scheduled(initialDelay = 10000L, fixedDelayString = "${froelingreaderservice.collect-interval}")
    public void readAndPublishData()
    {
        try
        {
            FroelingS3200LogData froelingS3200LogData = this.froelingReader.getData();

            // TODO: first implement stuff in API

            logger.info("here :)");
        }
        catch (Exception e)
        {
            logger.error("Unable to retrieve and publish data: {}", LoggingUtils.getLogForException(e));
        }
    }
}
