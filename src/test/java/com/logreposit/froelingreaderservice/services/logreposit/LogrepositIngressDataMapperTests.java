package com.logreposit.froelingreaderservice.services.logreposit;

import com.logreposit.froelingreaderservice.services.froelingreader.models.FroelingS3200LogData;
import com.logreposit.froelingreaderservice.services.froelingreader.models.FroelingS3200Reading;
import com.logreposit.froelingreaderservice.services.logreposit.dtos.ingress.DataType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

public class LogrepositIngressDataMapperTests
{
    private LogrepositIngressDataMapper logrepositIngressDataMapper;

    @BeforeEach
    public void setUp() {
        this.logrepositIngressDataMapper = new LogrepositIngressDataMapper();
    }

    @Test
    public void testToLogrepositIngressData() throws ParseException
    {
        final var ingressData = logrepositIngressDataMapper.toLogrepositIngressData(sampleLogData());

        assertThat(ingressData).isNotNull();

        assertSoftly(softly -> {
            softly.assertThat(ingressData.readings()).hasSize(2);

            final var reading1 = ingressData.readings().get(0);

            softly.assertThat(reading1.date()).isEqualTo("2022-05-10T18:27:05.525Z");
            softly.assertThat(reading1.measurement()).isEqualTo("data");
            softly.assertThat(reading1.tags()).hasSize(3);
            softly.assertThat(reading1.tags().get(0).name()).isEqualTo("address");
            softly.assertThat(reading1.tags().get(0).value()).isEqualTo("0x0000");
            softly.assertThat(reading1.tags().get(1).name()).isEqualTo("description");
            softly.assertThat(reading1.tags().get(1).value()).isEqualTo("Kesseltemperatur");
            softly.assertThat(reading1.tags().get(2).name()).isEqualTo("unit");
            softly.assertThat(reading1.tags().get(2).value()).isEqualTo("degrees_celsius");
            softly.assertThat(reading1.fields()).hasSize(1);
            softly.assertThat(reading1.fields().get(0).getDatatype()).isEqualTo(DataType.INTEGER);
            softly.assertThat(reading1.fields().get(0).getName()).isEqualTo("value");
            softly.assertThat(reading1.fields().get(0).getValue()).isEqualTo(79L);

            final var reading2 = ingressData.readings().get(1);

            softly.assertThat(reading2.date()).isEqualTo("2022-05-10T18:27:05.525Z");
            softly.assertThat(reading2.measurement()).isEqualTo("data");
            softly.assertThat(reading2.tags()).hasSize(3);
            softly.assertThat(reading2.tags().get(0).name()).isEqualTo("address");
            softly.assertThat(reading2.tags().get(0).value()).isEqualTo("0x00c8");
            softly.assertThat(reading2.tags().get(1).name()).isEqualTo("description");
            softly.assertThat(reading2.tags().get(1).value()).isEqualTo("Drehzahl der Pumpe");
            softly.assertThat(reading2.tags().get(2).name()).isEqualTo("unit");
            softly.assertThat(reading2.tags().get(2).value()).isEqualTo("%");
            softly.assertThat(reading2.fields()).hasSize(1);
            softly.assertThat(reading2.fields().get(0).getDatatype()).isEqualTo(DataType.INTEGER);
            softly.assertThat(reading2.fields().get(0).getName()).isEqualTo("value");
            softly.assertThat(reading2.fields().get(0).getValue()).isEqualTo(72L);
        });
    }

    private FroelingS3200LogData sampleLogData() throws ParseException
    {
        final var heatingBoilerTemperature = new FroelingS3200Reading();

        heatingBoilerTemperature.setAddress("0x0000");
        heatingBoilerTemperature.setDescription("Kesseltemperatur");
        heatingBoilerTemperature.setUnit("degrees_celsius");
        heatingBoilerTemperature.setValue(79);

        final var pumpSpeed = new FroelingS3200Reading();

        pumpSpeed.setAddress("0x00c8");
        pumpSpeed.setDescription("Drehzahl der Pumpe");
        pumpSpeed.setUnit("%");
        pumpSpeed.setValue(72);

        final var date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS").parse("10/05/2022 20:27:05.525");

        final var logData = new FroelingS3200LogData();

        logData.setDate(date);
        logData.setReadings(List.of(heatingBoilerTemperature, pumpSpeed));

        return logData;
    }
}
