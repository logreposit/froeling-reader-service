package com.logreposit.froelingreaderservice.communication.http.logrepositapi;

import com.logreposit.froelingreaderservice.services.froelingreader.models.FroelingS3200LogData;

public interface LogrepositApiClient
{
    void publishData(FroelingS3200LogData froelingS3200LogData) throws LogrepositApiClientException;
}
