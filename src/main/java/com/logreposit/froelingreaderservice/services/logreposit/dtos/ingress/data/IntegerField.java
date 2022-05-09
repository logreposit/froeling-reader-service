package com.logreposit.froelingreaderservice.services.logreposit.dtos.ingress.data;

import com.logreposit.froelingreaderservice.services.logreposit.dtos.ingress.DataType;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public final class IntegerField extends Field<Long>
{
    private final Long value;

    public IntegerField(String name, Long value)
    {
        super(name, DataType.INTEGER);

        this.value = value;
    }
}
