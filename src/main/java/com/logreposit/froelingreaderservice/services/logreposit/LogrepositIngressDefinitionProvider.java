package com.logreposit.froelingreaderservice.services.logreposit;

import com.logreposit.froelingreaderservice.services.logreposit.dtos.ingress.DataType;
import com.logreposit.froelingreaderservice.services.logreposit.dtos.ingress.definition.FieldDefinition;
import com.logreposit.froelingreaderservice.services.logreposit.dtos.ingress.definition.IngressDefinition;
import com.logreposit.froelingreaderservice.services.logreposit.dtos.ingress.definition.MeasurementDefinition;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class LogrepositIngressDefinitionProvider
{
    public IngressDefinition getIngressDefinition() {
        return new IngressDefinition(
                List.of(
                        new MeasurementDefinition(
                                "data",
                                Set.of("address", "description", "unit"),
                                List.of(
                                        new FieldDefinition("value", DataType.INTEGER, "value")
                                )
                        )
                )
        );
    }
}
