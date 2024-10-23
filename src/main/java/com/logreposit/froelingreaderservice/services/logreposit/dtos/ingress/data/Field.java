package com.logreposit.froelingreaderservice.services.logreposit.dtos.ingress.data;

import com.logreposit.froelingreaderservice.services.logreposit.dtos.ingress.DataType;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public abstract sealed class Field<T> permits FloatField, IntegerField, StringField
{
    private final String   name;
    private final DataType datatype;

    public Field(String name, DataType datatype) {
        this.name = name;
        this.datatype = datatype;
    }

    public abstract T getValue();
}
