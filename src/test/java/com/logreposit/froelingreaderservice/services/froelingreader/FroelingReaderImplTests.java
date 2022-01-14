package com.logreposit.froelingreaderservice.services.froelingreader;

import com.logreposit.froelingreaderservice.services.froelingreader.exceptions.FroelingClientException;
import com.logreposit.froelingreaderservice.services.froelingreader.exceptions.FroelingReaderException;
import com.logreposit.froelingreaderservice.services.froelingreader.models.FroelingS3200LogData;
import com.logreposit.froelingreaderservice.services.froelingreader.models.FroelingValueAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@ExtendWith(MockitoExtension.class)
public class FroelingReaderImplTests
{
    private FroelingReaderImpl froelingReader;

    @MockBean
    private FroelingClient froelingClient;

    @BeforeEach
    public void setUp()
    {
        this.froelingReader = new FroelingReaderImpl(this.froelingClient);
    }

    @Test
    public void testGetData() throws FroelingClientException, FroelingReaderException
    {
        List<FroelingValueAddress> valueAddresses = new ArrayList<>();

        FroelingValueAddress froelingValueAddress1 = new FroelingValueAddress(0, "0x000", 1, "", "1234", "Descr1");
        FroelingValueAddress froelingValueAddress2 = new FroelingValueAddress(1, "0x010", 2, "%", "1224", "Descr2");

        valueAddresses.add(froelingValueAddress1);
        valueAddresses.add(froelingValueAddress2);

        Mockito.when(this.froelingClient.getValueAddresses()).thenReturn(valueAddresses);
        Mockito.when(this.froelingClient.getValue("0x000", 1)).thenReturn(150);
        Mockito.when(this.froelingClient.getValue("0x010", 2)).thenReturn(350);

        FroelingS3200LogData froelingS3200LogData = this.froelingReader.getData();

        Mockito.verify(this.froelingClient, Mockito.times(1)).getValueAddresses();
        Mockito.verify(this.froelingClient, Mockito.times(1)).getValue(Mockito.eq("0x000"), Mockito.eq(1));
        Mockito.verify(this.froelingClient, Mockito.times(1)).getValue(Mockito.eq("0x010"), Mockito.eq(2));

        assertThat(froelingS3200LogData).isNotNull();

        assertSoftly(softly -> {
            softly.assertThat(froelingS3200LogData.getDate()).isNotNull();

            var readings = froelingS3200LogData.getReadings();

            softly.assertThat(readings).hasSize(2);

            softly.assertThat(readings.get(0).getAddress()).isEqualTo("0x000");
            softly.assertThat(readings.get(0).getDescription()).isEqualTo("Descr1");
            softly.assertThat(readings.get(0).getUnit()).isNull();
            softly.assertThat(readings.get(0).getValue()).isEqualTo(150);

            softly.assertThat(readings.get(1).getAddress()).isEqualTo("0x010");
            softly.assertThat(readings.get(1).getDescription()).isEqualTo("Descr2");
            softly.assertThat(readings.get(1).getUnit()).isEqualTo("%");
            softly.assertThat(readings.get(1).getValue()).isEqualTo(350);
        });
    }
}
