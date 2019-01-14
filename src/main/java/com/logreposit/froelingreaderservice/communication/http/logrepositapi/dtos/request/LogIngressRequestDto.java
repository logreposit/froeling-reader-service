package com.logreposit.froelingreaderservice.communication.http.logrepositapi.dtos.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.logreposit.froelingreaderservice.services.froelingreader.models.FroelingS3200LogData;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LogIngressRequestDto
{
    @JsonProperty(value = "deviceType")
    private DeviceType deviceType;

    @JsonProperty(value = "data")
    private FroelingS3200LogData data;

    public LogIngressRequestDto(DeviceType deviceType, FroelingS3200LogData froelingS3200LogData)
    {
        this.deviceType = deviceType;
        this.data       = froelingS3200LogData;
    }

    public DeviceType getDeviceType()
    {
        return this.deviceType;
    }

    public void setDeviceType(DeviceType deviceType)
    {
        this.deviceType = deviceType;
    }

    public FroelingS3200LogData getData()
    {
        return this.data;
    }

    public void setData(FroelingS3200LogData data)
    {
        this.data = data;
    }
}
