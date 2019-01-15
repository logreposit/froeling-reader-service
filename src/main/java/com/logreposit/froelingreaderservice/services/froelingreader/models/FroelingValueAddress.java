package com.logreposit.froelingreaderservice.services.froelingreader.models;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }

        if (o == null || this.getClass() != o.getClass())
        {
            return false;
        }

        FroelingValueAddress that = (FroelingValueAddress) o;

        return new EqualsBuilder().append(this.address, that.address).isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37).append(this.address).toHashCode();
    }
}
