package com.logreposit.froelingreaderservice.services.froelingreader.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FroelingLogData
{
    private Date                  date;
    private List<FroelingReading> readings;

    public FroelingLogData()
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

    public List<FroelingReading> getReadings()
    {
        return this.readings;
    }

    public void setReadings(List<FroelingReading> readings)
    {
        this.readings = readings;
    }
}
