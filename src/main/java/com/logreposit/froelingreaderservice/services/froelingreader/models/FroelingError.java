package com.logreposit.froelingreaderservice.services.froelingreader.models;

import java.util.Date;

public class FroelingError
{
    private final Date    date;
    private final Integer numberOne;
    private final Integer numberTwo;
    private final String  description;
    private final String  state;

    public FroelingError(Date date, Integer numberOne, Integer numberTwo, String description, String state)
    {
        this.date        = date;
        this.numberOne   = numberOne;
        this.numberTwo   = numberTwo;
        this.description = description;
        this.state       = state;
    }

    public Date getDate()
    {
        return this.date;
    }

    public Integer getNumberOne()
    {
        return this.numberOne;
    }

    public Integer getNumberTwo()
    {
        return this.numberTwo;
    }

    public String getDescription()
    {
        return this.description;
    }

    public String getState()
    {
        return this.state;
    }
}
