package com.logreposit.froelingreaderservice.services.froelingreader;

import com.logreposit.froelingreaderservice.services.froelingreader.exceptions.FroelingLogrepositServiceException;

public interface FroelingLogrepositService
{
    void readAndPublishData() throws FroelingLogrepositServiceException;
}
