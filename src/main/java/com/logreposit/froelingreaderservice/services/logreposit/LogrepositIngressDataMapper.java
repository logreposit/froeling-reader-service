package com.logreposit.froelingreaderservice.services.logreposit;

import com.logreposit.froelingreaderservice.services.froelingreader.models.FroelingS3200LogData;
import com.logreposit.froelingreaderservice.services.froelingreader.models.FroelingS3200Reading;
import com.logreposit.froelingreaderservice.services.logreposit.dtos.ingress.data.IngressData;
import com.logreposit.froelingreaderservice.services.logreposit.dtos.ingress.data.IntegerField;
import com.logreposit.froelingreaderservice.services.logreposit.dtos.ingress.data.Reading;
import com.logreposit.froelingreaderservice.services.logreposit.dtos.ingress.data.Tag;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
public class LogrepositIngressDataMapper
{
    public IngressData toLogrepositIngressData(FroelingS3200LogData froelingS3200LogData) {
        final var readings = froelingS3200LogData.getReadings()
                                                 .stream()
                                                 .map(r -> convert(froelingS3200LogData.getDate().toInstant(), r))
                                                 .toList();

        return new IngressData(readings);
    }

    private Reading convert(Instant date, FroelingS3200Reading reading) {
        return new Reading(date, "data", buildTags(reading), List.of(buildValueField(reading)));
    }

    private List<Tag> buildTags(FroelingS3200Reading reading) {
        final var addressTag = Optional.ofNullable(reading.getAddress()).map(a -> new Tag("address", a));
        final var descriptionTag = Optional.ofNullable(reading.getDescription()).map(d -> new Tag("description", d));
        final var unitTag = Optional.ofNullable(reading.getUnit()).map(u -> new Tag("unit", u));

        return Stream.of(addressTag, descriptionTag, unitTag).filter(Optional::isPresent).map(Optional::get).toList();
    }

    private IntegerField buildValueField(FroelingS3200Reading reading) {
        return new IntegerField("value", (long) reading.getValue());
    }
}
