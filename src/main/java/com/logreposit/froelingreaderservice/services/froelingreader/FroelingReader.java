package com.logreposit.froelingreaderservice.services.froelingreader;

import com.logreposit.froelingreaderservice.services.froelingreader.exceptions.FroelingReaderException;
import com.logreposit.froelingreaderservice.services.froelingreader.models.FroelingLogData;

public interface FroelingReader
{
    FroelingLogData getData() throws FroelingReaderException;
}
