package com.logreposit.froelingreaderservice.services.froelingreader;

import com.logreposit.froelingreaderservice.services.froelingreader.exceptions.FroelingOutputParserException;
import com.logreposit.froelingreaderservice.services.froelingreader.models.FroelingError;
import com.logreposit.froelingreaderservice.services.froelingreader.models.FroelingState;
import com.logreposit.froelingreaderservice.services.froelingreader.models.FroelingValueAddress;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@RunWith(SpringRunner.class)
public class FroelingOutputParserImplTests
{
    private FroelingOutputParserImpl froelingOutputParser;

    @Before
    public void setUp()
    {
        this.froelingOutputParser = new FroelingOutputParserImpl();
    }

    @Test
    public void testParseState() throws IOException, FroelingOutputParserException
    {
        String stateResponse = readFileFromTestResources("p4output/get-state.txt");

        Assert.assertNotNull(stateResponse);

        FroelingState froelingState = this.froelingOutputParser.parseState(stateResponse);

        Assert.assertNotNull(froelingState);
        Assert.assertEquals("50.04.05.09", froelingState.getVersion());
        Assert.assertEquals(1546111931000L, froelingState.getDate().getTime());
        Assert.assertEquals(2, froelingState.getDetails().size());
        Assert.assertEquals("Winterbetrieb", froelingState.getDetails().get(0));
        Assert.assertEquals("Heizen", froelingState.getDetails().get(3));
    }

    @Test
    public void testParseErrors() throws IOException, FroelingOutputParserException
    {
        String errorsResponse = readFileFromTestResources("p4output/get-errors.txt");

        Assert.assertNotNull(errorsResponse);

        List<FroelingError> froelingErrors = this.froelingOutputParser.parseErrors(errorsResponse);

        Calendar calendar = Calendar.getInstance();

        Assert.assertNotNull(froelingErrors);
        Assert.assertEquals(3, froelingErrors.size());

        calendar.setTime(froelingErrors.get(0).getDate());
        Assert.assertEquals(24, calendar.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(11, calendar.get(Calendar.MONTH));
        Assert.assertEquals(1, froelingErrors.get(0).getNumberOne(), 0);
        Assert.assertEquals(195, froelingErrors.get(0).getNumberTwo(), 0);
        Assert.assertEquals("Kesseltemperaturfühler fehlerhaft", froelingErrors.get(0).getDescription());
        Assert.assertEquals("gekommen", froelingErrors.get(0).getState());

        calendar.setTime(froelingErrors.get(1).getDate());
        Assert.assertEquals(24, calendar.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(11, calendar.get(Calendar.MONTH));
        Assert.assertEquals(1, froelingErrors.get(1).getNumberOne(), 0);
        Assert.assertEquals(67, froelingErrors.get(1).getNumberTwo(), 0);
        Assert.assertEquals("Kesseltemperaturfühler fehlerhaft", froelingErrors.get(1).getDescription());
        Assert.assertEquals("quittiert", froelingErrors.get(1).getState());

        calendar.setTime(froelingErrors.get(2).getDate());
        Assert.assertEquals(24, calendar.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(11, calendar.get(Calendar.MONTH));
        Assert.assertEquals(1, froelingErrors.get(2).getNumberOne(), 0);
        Assert.assertEquals(66, froelingErrors.get(2).getNumberTwo(), 0);
        Assert.assertEquals("Kesseltemperaturfühler fehlerhaft", froelingErrors.get(2).getDescription());
        Assert.assertEquals("gegangen", froelingErrors.get(2).getState());
    }

    @Test
    public void testParseValueAddresses() throws IOException, FroelingOutputParserException
    {
        String valuesResponse = readFileFromTestResources("p4output/get-values.txt");

        Assert.assertNotNull(valuesResponse);

        List<FroelingValueAddress> froelingValueAddresses = this.froelingOutputParser.parseValueAddresses(valuesResponse);

        Assert.assertNotNull(froelingValueAddresses);
        Assert.assertEquals(185, froelingValueAddresses.size());

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

        Assert.assertNotNull(valuesResponse);

        try
        {
            this.froelingOutputParser.parseValueAddresses(valuesResponse);

            Assert.fail("should not be here");
        }
        catch (FroelingOutputParserException e)
        {
            Assert.assertEquals("Unable to parse value addresses.", e.getMessage());
        }
        catch (Exception e)
        {
            Assert.fail("should not be here.");
        }
    }

    @Test
    public void testParseValue() throws FroelingOutputParserException
    {
        String response = "value 0x8 is -254";
        int    value    = this.froelingOutputParser.parseValue(response);

        Assert.assertEquals(-254, value, 0);
    }

    @Test
    public void testParseValue_invalidAddress()
    {
        String response = "Getting value failed, error -993";

        try
        {
            this.froelingOutputParser.parseValue(response);

            Assert.fail("should not be here.");
        }
        catch (FroelingOutputParserException e)
        {
            Assert.assertNotNull(e.getMessage());
            Assert.assertEquals("Unable to read value: Getting value failed, error -993", e.getMessage());
        }
        catch (Exception e)
        {
            Assert.fail("should not be here.");
        }
    }

    private static void compareValueAddress(FroelingValueAddress actual,
                                            Integer expectedNumber,
                                            String expectedAddress,
                                            Integer multiplier,
                                            String unit,
                                            String other,
                                            String description)
    {
        Assert.assertNotNull(actual);
        Assert.assertEquals(expectedNumber, actual.getNumber());
        Assert.assertEquals(expectedAddress, actual.getAddress());
        Assert.assertEquals(multiplier, actual.getMultiplier());
        Assert.assertEquals(unit, actual.getUnit());
        Assert.assertEquals(other, actual.getOther());
        Assert.assertEquals(description, actual.getDescription());
    }

    private static String readFileFromTestResources(String path) throws IOException
    {
        URL    url            = Thread.currentThread().getContextClassLoader().getResource(path);
        byte[] encodedContent = Files.readAllBytes(Paths.get(url.getPath()));
        String content        = new String(encodedContent, StandardCharsets.UTF_8);

        return content;
    }
}
