package com.logreposit.froelingreaderservice.services.froelingreader.models;

public class FroelingValueAddress
{
    private final Integer number;
    private final String  address;
    private final Integer multiplier;
    private final String  unit;
    private final String  other;
    private final String  description;

    public FroelingValueAddress(Integer number, String address, Integer multiplier, String unit, String other, String description)
    {
        this.number      = number;
        this.address     = address;
        this.multiplier  = multiplier;
        this.unit        = unit;
        this.other       = other;
        this.description = description;
    }

    public Integer getNumber()
    {
        return this.number;
    }

    public String getAddress()
    {
        return this.address;
    }

    public Integer getMultiplier()
    {
        return this.multiplier;
    }

    public String getUnit()
    {
        return this.unit;
    }

    public String getOther()
    {
        return this.other;
    }

    public String getDescription()
    {
        return this.description;
    }
}
