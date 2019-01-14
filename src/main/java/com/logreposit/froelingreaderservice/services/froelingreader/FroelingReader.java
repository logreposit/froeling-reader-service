package com.logreposit.froelingreaderservice.services.froelingreader;

import com.logreposit.froelingreaderservice.services.froelingreader.exceptions.FroelingReaderException;
import com.logreposit.froelingreaderservice.services.froelingreader.models.FroelingS3200LogData;

public interface FroelingReader
{
    FroelingS3200LogData getData() throws FroelingReaderException;
}
