package com.logreposit.froelingreaderservice.communication.http.logrepositapi.dtos.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.logreposit.froelingreaderservice.services.froelingreader.models.FroelingLogData;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LogIngressRequestDto
{
    @JsonProperty(value = "deviceType")
    private DeviceType deviceType;

    @JsonProperty(value = "data")
    private FroelingLogData data;

    public LogIngressRequestDto(DeviceType deviceType, FroelingLogData froelingLogData)
    {
        this.deviceType = deviceType;
        this.data       = froelingLogData;
    }

    public DeviceType getDeviceType()
    {
        return this.deviceType;
    }

    public void setDeviceType(DeviceType deviceType)
    {
        this.deviceType = deviceType;
    }

    public FroelingLogData getData()
    {
        return this.data;
    }

    public void setData(FroelingLogData data)
    {
        this.data = data;
    }
}
