package com.logreposit.froelingreaderservice.communication.http.logrepositapi;

import com.logreposit.froelingreaderservice.services.froelingreader.models.FroelingLogData;

public interface LogrepositApiClient
{
    void publishData(FroelingLogData cmiLogData) throws LogrepositApiClientException;
}
