package com.logreposit.froelingreaderservice.services.froelingreader.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FroelingS3200LogData
{
    private Date                       date;
    private List<FroelingS3200Reading> readings;

    public FroelingS3200LogData()
    {
        this.readings = new ArrayList<>();
    }

    public Date getDate()
    {
        return this.date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public List<FroelingS3200Reading> getReadings()
    {
        return this.readings;
    }

    public void setReadings(List<FroelingS3200Reading> readings)
    {
        this.readings = readings;
    }
}
