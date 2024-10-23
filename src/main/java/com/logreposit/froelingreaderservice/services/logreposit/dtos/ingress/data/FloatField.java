package com.logreposit.froelingreaderservice.services.logreposit.dtos.ingress.data;

import com.logreposit.froelingreaderservice.services.logreposit.dtos.ingress.DataType;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public final class FloatField extends Field<Double>
{
    private final Double value;

    public FloatField(String name, Double value)
    {
        super(name, DataType.FLOAT);

        this.value = value;
    }
}
