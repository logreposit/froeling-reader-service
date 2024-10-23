package com.logreposit.froelingreaderservice.services.logreposit.dtos.ingress.data;

import com.logreposit.froelingreaderservice.services.logreposit.dtos.ingress.DataType;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public final class StringField extends Field<String>
{
    private final String value;

    public StringField(String name, String value)
    {
        super(name, DataType.STRING);

        this.value = value;
    }
}
