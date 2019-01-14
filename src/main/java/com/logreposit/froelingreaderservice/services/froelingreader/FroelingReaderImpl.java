package com.logreposit.froelingreaderservice.services.froelingreader;

import com.logreposit.froelingreaderservice.services.froelingreader.exceptions.FroelingClientException;
import com.logreposit.froelingreaderservice.services.froelingreader.exceptions.FroelingReaderException;
import com.logreposit.froelingreaderservice.services.froelingreader.models.FroelingS3200LogData;
import com.logreposit.froelingreaderservice.services.froelingreader.models.FroelingValueAddress;
import com.logreposit.froelingreaderservice.services.froelingreader.models.FroelingS3200Reading;
import com.logreposit.froelingreaderservice.utils.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class FroelingReaderImpl implements FroelingReader
{
    private static final Logger logger = LoggerFactory.getLogger(FroelingReaderImpl.class);

    private final FroelingClient froelingClient;

    private List<FroelingValueAddress> valueAddresses;

    public FroelingReaderImpl(FroelingClient froelingClient)
    {
        this.valueAddresses = new ArrayList<>();
        this.froelingClient = froelingClient;
    }

    @Override
    public FroelingS3200LogData getData() throws FroelingReaderException
    {
        if (CollectionUtils.isEmpty(this.valueAddresses))
        {
            this.retrieveValueAddresses();
        }

        List<FroelingS3200Reading> readings        = this.retrieveReadings();
        FroelingS3200LogData froelingS3200LogData = new FroelingS3200LogData();

        froelingS3200LogData.setDate(new Date());
        froelingS3200LogData.setReadings(readings);

        return froelingS3200LogData;
    }

    private List<FroelingS3200Reading> retrieveReadings() throws FroelingReaderException
    {
        List<FroelingS3200Reading> readings = new ArrayList<>();

        for (FroelingValueAddress froelingValueAddress : this.valueAddresses)
        {
            FroelingS3200Reading reading = this.retrieveReading(froelingValueAddress);

            readings.add(reading);
        }

        return readings;
    }

    private void retrieveValueAddresses() throws FroelingReaderException
    {
        try
        {
            List<FroelingValueAddress> froelingValueAddresses = this.froelingClient.getValueAddresses();

            this.valueAddresses = froelingValueAddresses;
        }
        catch (FroelingClientException e)
        {
            logger.error("Unable to retrieve Froeling Value Addresses: {}", LoggingUtils.getLogForException(e));

            throw new FroelingReaderException("Unable to retrieve Froeling Value Addresses", e);
        }
    }

    private FroelingS3200Reading retrieveReading(FroelingValueAddress froelingValueAddress) throws FroelingReaderException
    {
        int             value   = this.retrieveFroelingValue(froelingValueAddress.getAddress(), froelingValueAddress.getMultiplier());
        FroelingS3200Reading reading = new FroelingS3200Reading();

        reading.setAddress(froelingValueAddress.getAddress());
        reading.setValue(value);
        reading.setDescription(froelingValueAddress.getDescription());

        if (!StringUtils.isEmpty(froelingValueAddress.getUnit()))
        {
            reading.setUnit(froelingValueAddress.getUnit());
        }

        return reading;
    }

    private int retrieveFroelingValue(String address, Integer multiplier) throws FroelingReaderException
    {
        try
        {
            int value = this.froelingClient.getValue(address, multiplier);

            return value;
        }
        catch (FroelingClientException e)
        {
            logger.error("Caught FroelingClientException while retrieving value for address {}: {}", address, LoggingUtils.getLogForException(e));

            throw new FroelingReaderException("Caught FroelingClientException while retrieving value", e);
        }
    }
}
