package com.logreposit.froelingreaderservice.services.froelingreader;

import com.logreposit.froelingreaderservice.services.froelingreader.exceptions.FroelingOutputParserException;
import com.logreposit.froelingreaderservice.services.froelingreader.models.FroelingError;
import com.logreposit.froelingreaderservice.services.froelingreader.models.FroelingState;
import com.logreposit.froelingreaderservice.services.froelingreader.models.FroelingValueAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class FroelingOutputParserImpl implements FroelingOutputParser
{
    private static final Logger logger = LoggerFactory.getLogger(FroelingOutputParserImpl.class);

    private static final String  STATE_DATE_FORMAT = "EEEE, dd. MMM. yyyy HH:mm:ss";
    private static final String  ERROR_DATE_FORMAT = "EEE MMM dd HH:mm:ss";

    private static final Pattern VALUE_PATTERN         = Pattern.compile("^value (0x[0-9a-f]+) is (\\-?([0-9]+))$");
    private static final Pattern VALUE_ADDRESS_PATTERN = Pattern.compile("^(\\s+)?([0-9]+)\\)\\s((0x)([0-9a-f]+))\\s+([0-9]+)\\s'(.+)?'\\s\\(([0-9]+)\\)\\s'(.+)'$");
    private static final Pattern ERROR_PATTERN         = Pattern.compile("^(.+):\\s\\s([0-9]+)/([0-9]+)\\s\\s'(.+)' \\- (.+)$");
    private static final Pattern VERSION_PATTERN       = Pattern.compile("^Version: (.+)$");
    private static final Pattern STATE_PATTERN         = Pattern.compile("^([0-9]+) \\- (.*)$");
    private static final Pattern DATE_PATTERN          = Pattern.compile("^Time: (.+)$");

    private final SimpleDateFormat stateDateTimeFormatter;
    private final SimpleDateFormat errorDateTimeFormatter;

    public FroelingOutputParserImpl()
    {
        this.stateDateTimeFormatter = new SimpleDateFormat(STATE_DATE_FORMAT);
        this.errorDateTimeFormatter = new SimpleDateFormat(ERROR_DATE_FORMAT);
    }

    @Override
    public FroelingState parseState(String stdout) throws FroelingOutputParserException
    {
        String[] lines = stdout.split("\\r?\\n");

        if (lines.length < 2)
        {
            logger.error("Received invalid state response. A valid state response has at least two lines.");

            throw new FroelingOutputParserException("Received invalid state response. A valid state response has at least two lines");
        }

        String               versionLine   = lines[0];
        String               dateLine      = lines[1];
        String               version       = parseVersion(versionLine);
        Date                 date          = this.parseStateDate(dateLine);
        Map<Integer, String> states        = parseStates(lines);
        FroelingState        froelingState = new FroelingState(version, date, states);

        return froelingState;
    }

    @Override
    public List<FroelingError> parseErrors(String stdout) throws FroelingOutputParserException
    {
        String[] lines = stdout.split("\\r?\\n");

        List<FroelingError> froelingErrors = new ArrayList<>();

        for (String line : lines)
        {
            FroelingError froelingError = this.parseErrorLine(line);

            froelingErrors.add(froelingError);
        }

        return froelingErrors;
    }

    @Override
    public List<FroelingValueAddress> parseValueAddresses(String stdout) throws FroelingOutputParserException
    {
        String[] lines = stdout.split("\\r?\\n");

        Set<FroelingValueAddress> froelingValueAddresses = new HashSet<>();

        for (String line : lines)
        {
            Optional<FroelingValueAddress> froelingValueAddress = this.parseValueAddress(line);

            froelingValueAddress.ifPresent(froelingValueAddresses::add);
        }

        if (froelingValueAddresses.isEmpty())
        {
            logger.error("Unable to parse value addresses.");

            throw new FroelingOutputParserException("Unable to parse value addresses.");
        }

        List<FroelingValueAddress> froelingValueAddressesSorted = new ArrayList<>(froelingValueAddresses);

        froelingValueAddressesSorted.sort(Comparator.comparing(FroelingValueAddress::getAddress));

        return froelingValueAddressesSorted;
    }

    @Override
    public int parseValue(String stdout) throws FroelingOutputParserException
    {
        Matcher matcher = VALUE_PATTERN.matcher(stdout);

        if (!matcher.matches())
        {
            String error = String.format("Unable to read value: %s", stdout);

            logger.error(error);

            throw new FroelingOutputParserException(error);
        }

        int value = Integer.parseInt(matcher.group(2));

        return value;
    }

    private static String parseVersion(String line) throws FroelingOutputParserException
    {
        Matcher matcher = VERSION_PATTERN.matcher(line);

        if (matcher.matches())
        {
            String version = matcher.group(1);

            return version;
        }

        throw new FroelingOutputParserException("Unable to parse version.");
    }

    private Date parseStateDate(String line) throws FroelingOutputParserException
    {
        Matcher matcher = DATE_PATTERN.matcher(line);

        if (matcher.matches())
        {
            String sourceDate = matcher.group(1);

            try
            {
                Date date = this.stateDateTimeFormatter.parse(sourceDate);

                return date;
            }
            catch (ParseException e)
            {
                logger.error("Unable to convert date");

                throw new FroelingOutputParserException("Unable to convert date");
            }
        }

        logger.error("Unable to parse date.");

        throw new FroelingOutputParserException("Unable to parse date");
    }

    private static Map<Integer, String> parseStates(String[] lines)
    {
        Map<Integer, String> states = new HashMap<>();

        for (int i = 2; i < lines.length; i++)
        {
            String  line    = lines[i];
            Matcher matcher = STATE_PATTERN.matcher(line);

            if (matcher.matches())
            {
                String  number  = matcher.group(1);
                String  state   = matcher.group(2);

                states.put(Integer.valueOf(number), state);
            }
        }

        return states;
    }

    private FroelingError parseErrorLine(String line) throws FroelingOutputParserException
    {
        Matcher matcher = ERROR_PATTERN.matcher(line);

        if (matcher.matches())
        {
            String        sourceDate       = matcher.group(1);
            String        sourceNumberOne  = matcher.group(2);
            String        sourceNumberTwo  = matcher.group(3);
            String        text             = matcher.group(4);
            String        state            = matcher.group(5);
            Date          date             = this.parseErrorDate(sourceDate);
            Integer       numberOne        = Integer.valueOf(sourceNumberOne);
            Integer       numberTwo        = Integer.valueOf(sourceNumberTwo);
            FroelingError froelingError    = new FroelingError(date, numberOne, numberTwo, text, state);

            return froelingError;
        }

        throw new FroelingOutputParserException("Could not parse error");
    }

    private Date parseErrorDate(String dateString) throws FroelingOutputParserException
    {
        try
        {
            Date date = this.errorDateTimeFormatter.parse(dateString);

            return date;
        }
        catch (ParseException e)
        {
            logger.error("Unable to convert date");

            throw new FroelingOutputParserException("Unable to convert date");
        }
    }

    private Optional<FroelingValueAddress> parseValueAddress(String line)
    {
        Matcher matcher = VALUE_ADDRESS_PATTERN.matcher(line);

        if (matcher.matches())
        {
            Integer              number               = Integer.valueOf(matcher.group(2));
            String               address              = matcher.group(3);
            Integer              multiplier           = Integer.valueOf(matcher.group(6));
            String               unit                 = matcher.group(7);
            String               other                = matcher.group(8);
            String               description          = matcher.group(9);
            FroelingValueAddress froelingValueAddress = new FroelingValueAddress(number, address, multiplier, unit, other, description);

            return Optional.of(froelingValueAddress);
        }

        logger.warn("Could not parse value address");

        return Optional.empty();
    }
}
