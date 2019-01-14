package com.logreposit.froelingreaderservice.services.froelingreader;

import com.logreposit.froelingreaderservice.communication.http.logrepositapi.LogrepositApiClient;
import com.logreposit.froelingreaderservice.services.froelingreader.exceptions.FroelingLogrepositServiceException;
import com.logreposit.froelingreaderservice.services.froelingreader.models.FroelingS3200LogData;
import com.logreposit.froelingreaderservice.utils.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class FroelingLogrepositServiceImpl implements FroelingLogrepositService
{
    private static final Logger logger = LoggerFactory.getLogger(FroelingLogrepositServiceImpl.class);

    private final FroelingReader      froelingReader;
    private final LogrepositApiClient logrepositApiClient;

    public FroelingLogrepositServiceImpl(FroelingReader      froelingReader,
                                         LogrepositApiClient logrepositApiClient)
    {
        this.froelingReader      = froelingReader;
        this.logrepositApiClient = logrepositApiClient;
    }

    @Override
    @Scheduled(initialDelay = 10000L, fixedDelayString = "${froelingreaderservice.collect-interval}")
    public void readAndPublishData() throws FroelingLogrepositServiceException
    {
        logger.debug("Started collecting log values.");

        Date                 begin                = new Date();
        FroelingS3200LogData froelingS3200LogData = this.collectLogData();
        long                 logFetchDuration     = ((new Date()).getTime() - begin.getTime()) / 1000;

        logger.info("Finished collecting and converting log values. Operation took {} seconds.", logFetchDuration);

        this.publishData(froelingS3200LogData);
    }

    private FroelingS3200LogData collectLogData() throws FroelingLogrepositServiceException
    {
        try
        {
            FroelingS3200LogData froelingS3200LogData = this.froelingReader.getData();

            return froelingS3200LogData;
        }
        catch (Exception e)
        {
            logger.error("Caught Exception while reading Data from Froeling Lambdatronic S3200: {}", LoggingUtils.getLogForException(e));

            throw new FroelingLogrepositServiceException("Caught Exception while reading Data from Froeling Lambdatronic S3200", e);
        }
    }

    private void publishData(FroelingS3200LogData froelingS3200LogData) throws FroelingLogrepositServiceException
    {
        try
        {
            this.logrepositApiClient.publishData(froelingS3200LogData);
        }
        catch (Exception e)
        {
            logger.error("Unable to publish froelingS3200LogData: {}", LoggingUtils.getLogForException(e));
            throw new FroelingLogrepositServiceException("Unable to publish froelingS3200LogData", e);
        }
    }
}
