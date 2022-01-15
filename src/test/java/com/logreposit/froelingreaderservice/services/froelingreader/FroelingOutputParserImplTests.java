package com.logreposit.froelingreaderservice.services.froelingreader;

import com.logreposit.froelingreaderservice.services.froelingreader.exceptions.FroelingOutputParserException;
import com.logreposit.froelingreaderservice.services.froelingreader.models.FroelingError;
import com.logreposit.froelingreaderservice.services.froelingreader.models.FroelingState;
import com.logreposit.froelingreaderservice.services.froelingreader.models.FroelingValueAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

public class FroelingOutputParserImplTests
{
    private FroelingOutputParserImpl froelingOutputParser;

    @BeforeEach
    public void setUp()
    {
        this.froelingOutputParser = new FroelingOutputParserImpl();
    }

    @Test
    public void testParseState() throws IOException, FroelingOutputParserException
    {
        String stateResponse = readFileFromTestResources("p4output/get-state.txt");

        assertThat(stateResponse).isNotNull();

        FroelingState froelingState = this.froelingOutputParser.parseState(stateResponse);

        assertThat(froelingState).isNotNull();

        assertSoftly(softly -> {
            softly.assertThat(froelingState.getVersion()).isEqualTo("50.04.05.09");
            softly.assertThat(froelingState.getDate().getTime()).isEqualTo(1546111931000L);
            softly.assertThat(froelingState.getDetails()).hasSize(2);
            softly.assertThat(froelingState.getDetails().get(0)).isEqualTo("Winterbetrieb");
            softly.assertThat(froelingState.getDetails().get(3)).isEqualTo("Heizen");
        });
    }

    @Test
    public void testParseErrors() throws IOException, FroelingOutputParserException
    {
        String errorsResponse = readFileFromTestResources("p4output/get-errors.txt");

        assertThat(errorsResponse).isNotNull();

        List<FroelingError> froelingErrors = this.froelingOutputParser.parseErrors(errorsResponse);

        assertThat(froelingErrors).hasSize(3);

        assertSoftly(softly -> {
            final var calendar = Calendar.getInstance();

            calendar.setTime(froelingErrors.get(0).getDate());

            softly.assertThat(calendar.get(Calendar.DAY_OF_MONTH)).isEqualTo(24);
            softly.assertThat(calendar.get(Calendar.MONTH)).isEqualTo(11);

            softly.assertThat(froelingErrors.get(0).getNumberOne()).isEqualTo(1);
            softly.assertThat(froelingErrors.get(0).getNumberTwo()).isEqualTo(195);
            softly.assertThat(froelingErrors.get(0).getDescription()).isEqualTo("Kesseltemperaturfühler fehlerhaft");
            softly.assertThat(froelingErrors.get(0).getState()).isEqualTo("gekommen");
        });

        assertSoftly(softly -> {
            final var calendar = Calendar.getInstance();

            calendar.setTime(froelingErrors.get(1).getDate());

            softly.assertThat(calendar.get(Calendar.DAY_OF_MONTH)).isEqualTo(24);
            softly.assertThat(calendar.get(Calendar.MONTH)).isEqualTo(11);

            softly.assertThat(froelingErrors.get(1).getNumberOne()).isEqualTo(1);
            softly.assertThat(froelingErrors.get(1).getNumberTwo()).isEqualTo(67);
            softly.assertThat(froelingErrors.get(1).getDescription()).isEqualTo("Kesseltemperaturfühler fehlerhaft");
            softly.assertThat(froelingErrors.get(1).getState()).isEqualTo("quittiert");
        });

        assertSoftly(softly -> {
            final var calendar = Calendar.getInstance();

            calendar.setTime(froelingErrors.get(2).getDate());

            softly.assertThat(calendar.get(Calendar.DAY_OF_MONTH)).isEqualTo(24);
            softly.assertThat(calendar.get(Calendar.MONTH)).isEqualTo(11);

            softly.assertThat(froelingErrors.get(2).getNumberOne()).isEqualTo(1);
            softly.assertThat(froelingErrors.get(2).getNumberTwo()).isEqualTo(66);
            softly.assertThat(froelingErrors.get(2).getDescription()).isEqualTo("Kesseltemperaturfühler fehlerhaft");
            softly.assertThat(froelingErrors.get(2).getState()).isEqualTo("gegangen");
        });
    }

    @Test
    public void testParseValueAddresses() throws IOException, FroelingOutputParserException
    {
        String valuesResponse = readFileFromTestResources("p4output/get-values.txt");

        assertThat(valuesResponse).isNotNull();

        List<FroelingValueAddress> froelingValueAddresses = this.froelingOutputParser.parseValueAddresses(valuesResponse);

        assertThat(froelingValueAddresses).hasSize(185);

        compareValueAddress(froelingValueAddresses.get(0), 0, "0x0000", 2, null, "0002", "Kesseltemperatur");
        compareValueAddress(froelingValueAddresses.get(1), 1, "0x0001", 1, null, "0003", "Abgastemperatur");
        compareValueAddress(froelingValueAddresses.get(2), 64, "0x0002", 2, null, "0002", "Boardtemperatur");
        compareValueAddress(froelingValueAddresses.get(3), 8, "0x0003", 10, "%", "0002", "Restsauerstoffgehalt");
        compareValueAddress(froelingValueAddresses.get(4), 119, "0x0004", 2, null, "0002", "Außentemperatur");
        compareValueAddress(froelingValueAddresses.get(5), 7, "0x0005", 1, "%", "0001", "Position der Primärluftklappe");
    }

    @Test
    public void testParseValueAddresses_emptyResponse()
    {
        String valuesResponse = "";

        assertThat(valuesResponse).isNotNull();

        assertThatThrownBy(() -> this.froelingOutputParser.parseValueAddresses(valuesResponse))
                .isExactlyInstanceOf(FroelingOutputParserException.class)
                .hasMessage("Unable to parse value addresses.");
    }

    @Test
    public void testParseValue() throws FroelingOutputParserException
    {
        // Older p4 version: tell(eloAlways, "value 0x%x is %d", v.address, v.value);

        String response = "value 0x8 is -254";
        int    value    = this.froelingOutputParser.parseValue(response);

        assertThat(value).isEqualTo(-254);
    }

    @Test
    public void testParseValueWithNewerCliVersion() throws FroelingOutputParserException
    {
        // Newer p4 version: tell(eloAlways, "value 0x%x is %d / %d", v.address, v.value, (word)v.value);

        String response = "value 0x8 is -254 / 789";
        int    value    = this.froelingOutputParser.parseValue(response);

        assertThat(value).isEqualTo(-254);
    }

    @Test
    public void testParseValue_invalidAddress()
    {
        String response = "Getting value failed, error -993";

        assertThatThrownBy(() -> this.froelingOutputParser.parseValue(response))
                .isExactlyInstanceOf(FroelingOutputParserException.class)
                .hasMessage("Unable to read value: Getting value failed, error -993");
    }

    private static void compareValueAddress(FroelingValueAddress actual,
                                            Integer expectedNumber,
                                            String expectedAddress,
                                            Integer multiplier,
                                            String unit,
                                            String other,
                                            String description)
    {
        assertSoftly(softly -> {
            assertThat(actual.getNumber()).isEqualTo(expectedNumber);
            assertThat(actual.getAddress()).isEqualTo(expectedAddress);
            assertThat(actual.getMultiplier()).isEqualTo(multiplier);
            assertThat(actual.getUnit()).isEqualTo(unit);
            assertThat(actual.getDescription()).isEqualTo(description);
        });
    }

    private static String readFileFromTestResources(String path) throws IOException
    {
        URL    url            = Thread.currentThread().getContextClassLoader().getResource(path);

        assertThat(url).isNotNull();

        byte[] encodedContent = Files.readAllBytes(Paths.get(url.getPath()));
        String content        = new String(encodedContent, StandardCharsets.UTF_8);

        return content;
    }
}
