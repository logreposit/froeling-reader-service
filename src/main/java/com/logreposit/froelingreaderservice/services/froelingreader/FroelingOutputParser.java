package com.logreposit.froelingreaderservice.services.froelingreader;

import com.logreposit.froelingreaderservice.services.froelingreader.exceptions.FroelingOutputParserException;
import com.logreposit.froelingreaderservice.services.froelingreader.models.FroelingError;
import com.logreposit.froelingreaderservice.services.froelingreader.models.FroelingState;
import com.logreposit.froelingreaderservice.services.froelingreader.models.FroelingValueAddress;

import java.util.List;

public interface FroelingOutputParser
{
    FroelingState              parseState          (String stdout) throws FroelingOutputParserException;
    List<FroelingError>        parseErrors         (String stdout) throws FroelingOutputParserException;
    List<FroelingValueAddress> parseValueAddresses (String stdout) throws FroelingOutputParserException;
    int                        parseValue          (String stdout) throws FroelingOutputParserException;
}
