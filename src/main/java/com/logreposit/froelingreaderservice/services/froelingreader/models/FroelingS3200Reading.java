package com.logreposit.froelingreaderservice.services.froelingreader.models;

public class FroelingS3200Reading
{
    private String address;
    private int    value;
    private String unit;
    private String description;

    public String getAddress()
    {
        return this.address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public int getValue()
    {
        return this.value;
    }

    public void setValue(int value)
    {
        this.value = value;
    }

    public String getUnit()
    {
        return this.unit;
    }

    public void setUnit(String unit)
    {
        this.unit = unit;
    }

    public String getDescription()
    {
        return this.description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }
}
