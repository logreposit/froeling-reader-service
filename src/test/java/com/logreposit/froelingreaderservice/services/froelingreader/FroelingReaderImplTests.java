package com.logreposit.froelingreaderservice.services.froelingreader;

import com.logreposit.froelingreaderservice.services.froelingreader.exceptions.FroelingClientException;
import com.logreposit.froelingreaderservice.services.froelingreader.exceptions.FroelingReaderException;
import com.logreposit.froelingreaderservice.services.froelingreader.models.FroelingS3200LogData;
import com.logreposit.froelingreaderservice.services.froelingreader.models.FroelingValueAddress;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
public class FroelingReaderImplTests
{
    private FroelingReaderImpl froelingReader;

    @MockBean
    private FroelingClient froelingClient;

    @Before
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

        Assert.assertNotNull(froelingS3200LogData);

        Mockito.verify(this.froelingClient, Mockito.times(1)).getValueAddresses();
        Mockito.verify(this.froelingClient, Mockito.times(1)).getValue(Mockito.eq("0x000"), Mockito.eq(1));
        Mockito.verify(this.froelingClient, Mockito.times(1)).getValue(Mockito.eq("0x010"), Mockito.eq(2));

        Assert.assertNotNull(froelingS3200LogData.getDate());
        Assert.assertNotNull(froelingS3200LogData.getReadings());
        Assert.assertEquals(2, froelingS3200LogData.getReadings().size());
        Assert.assertEquals("0x000", froelingS3200LogData.getReadings().get(0).getAddress());
        Assert.assertEquals("Descr1", froelingS3200LogData.getReadings().get(0).getDescription());
        Assert.assertNull(froelingS3200LogData.getReadings().get(0).getUnit());
        Assert.assertEquals(150, froelingS3200LogData.getReadings().get(0).getValue());
        Assert.assertEquals("0x010", froelingS3200LogData.getReadings().get(1).getAddress());
        Assert.assertEquals("Descr2", froelingS3200LogData.getReadings().get(1).getDescription());
        Assert.assertEquals("%", froelingS3200LogData.getReadings().get(1).getUnit());
        Assert.assertEquals(350, froelingS3200LogData.getReadings().get(1).getValue());
    }
}
