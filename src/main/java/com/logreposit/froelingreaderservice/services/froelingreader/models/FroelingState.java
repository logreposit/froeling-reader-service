package com.logreposit.froelingreaderservice.services.froelingreader.models;

import java.util.Date;
import java.util.Map;

public class FroelingState
{
    private final String               version;
    private final Date                 date;
    private final Map<Integer, String> details;

    public FroelingState(String version, Date date, Map<Integer, String> details)
    {
        this.version = version;
        this.date    = date;
        this.details = details;
    }

    public String getVersion()
    {
        return this.version;
    }

    public Date getDate()
    {
        return this.date;
    }

    public Map<Integer, String> getDetails()
    {
        return this.details;
    }
}
