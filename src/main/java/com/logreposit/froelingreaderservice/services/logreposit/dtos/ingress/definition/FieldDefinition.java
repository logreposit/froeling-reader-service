package com.logreposit.froelingreaderservice.services.logreposit.dtos.ingress.definition;

import com.logreposit.froelingreaderservice.services.logreposit.dtos.ingress.DataType;

public record FieldDefinition(
    String   name,
    DataType datatype,
    String   description) {}
