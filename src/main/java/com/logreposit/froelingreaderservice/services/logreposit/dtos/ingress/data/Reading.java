package com.logreposit.froelingreaderservice.services.logreposit.dtos.ingress.data;

import java.time.Instant;
import java.util.List;

public record Reading(
        Instant date,
        String measurement,
        List<Tag> tags,
        List<Field<?>> fields
) {}
