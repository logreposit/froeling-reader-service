package com.logreposit.froelingreaderservice.services.froelingreader;

import com.logreposit.froelingreaderservice.services.froelingreader.exceptions.FroelingClientException;
import com.logreposit.froelingreaderservice.services.froelingreader.models.FroelingError;
import com.logreposit.froelingreaderservice.services.froelingreader.models.FroelingState;
import com.logreposit.froelingreaderservice.services.froelingreader.models.FroelingValueAddress;

import java.util.List;

public interface FroelingClient
{
    FroelingState              getState          ()                                   throws FroelingClientException;
    List<FroelingError>        getErrors         ()                                   throws FroelingClientException;
    List<FroelingValueAddress> getValueAddresses ()                                   throws FroelingClientException;
    int                        getValue          (String address, Integer multiplier) throws FroelingClientException;
}
