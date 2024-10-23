package com.logreposit.froelingreaderservice.services.logreposit;

import com.logreposit.froelingreaderservice.configuration.ApplicationConfiguration;
import com.logreposit.froelingreaderservice.configuration.RetryConfiguration;
import com.logreposit.froelingreaderservice.services.logreposit.dtos.ingress.DataType;
import com.logreposit.froelingreaderservice.services.logreposit.dtos.ingress.data.FloatField;
import com.logreposit.froelingreaderservice.services.logreposit.dtos.ingress.data.IngressData;
import com.logreposit.froelingreaderservice.services.logreposit.dtos.ingress.data.IntegerField;
import com.logreposit.froelingreaderservice.services.logreposit.dtos.ingress.data.Reading;
import com.logreposit.froelingreaderservice.services.logreposit.dtos.ingress.data.StringField;
import com.logreposit.froelingreaderservice.services.logreposit.dtos.ingress.data.Tag;
import com.logreposit.froelingreaderservice.services.logreposit.dtos.ingress.definition.FieldDefinition;
import com.logreposit.froelingreaderservice.services.logreposit.dtos.ingress.definition.IngressDefinition;
import com.logreposit.froelingreaderservice.services.logreposit.dtos.ingress.definition.MeasurementDefinition;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.response.MockRestResponseCreators;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.matchesPattern;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;

@ExtendWith(SpringExtension.class)
@RestClientTest(LogrepositApiService.class)
@Import(RetryConfiguration.class)
public class LogrepositApiServiceTests {
    @MockBean
    private ApplicationConfiguration applicationConfiguration;

    @MockBean
    private LogrepositIngressDefinitionProvider ingressDefinitionProvider;

    @Autowired
    private LogrepositApiService client;

    @Autowired
    private MockRestServiceServer server;

    @BeforeEach
    public void setUp() {
        when(applicationConfiguration.getApiBaseUrl()).thenReturn("https://api.logreposit.local");
    }

    @Test
    void givenValidDataItShouldFinishSuccessfully() {
        server.expect(ExpectedCount.once(), requestTo("https://api.logreposit.local/v2/ingress/data"))
              .andExpect(method(HttpMethod.POST))
              .andExpect(jsonPath("$.readings").isArray())
              .andExpect(jsonPath("$.readings.length()").value(1))
              .andExpect(jsonPath("$.readings[0].date").isString())
              .andExpect(jsonPath("$.readings[0].date").value(matchesPattern("(\\d{4})-(\\d{2})-(\\d{2})T(\\d{2})\\:(\\d{2})\\:(\\d{2})(\\.\\d+)Z")))
              .andExpect(jsonPath("$.readings[0].tags").isArray())
              .andExpect(jsonPath("$.readings[0].tags.length()").value(1))
              .andExpect(jsonPath("$.readings[0].tags[0]").isMap())
              .andExpect(jsonPath("$.readings[0].tags[0].name").value("device_address"))
              .andExpect(jsonPath("$.readings[0].tags[0].value").value("1"))
              .andExpect(jsonPath("$.readings[0].fields").isArray())
              .andExpect(jsonPath("$.readings[0].fields.length()").value(3))
              .andExpect(jsonPath("$.readings[0].fields[?(@.name == \"battery_voltage\")].value").value(24.525))
              .andExpect(jsonPath("$.readings[0].fields[?(@.name == \"battery_voltage\")].datatype").value("FLOAT"))
              .andExpect(jsonPath("$.readings[0].fields[?(@.name == \"alarm_state\")].value").value(1))
              .andExpect(jsonPath("$.readings[0].fields[?(@.name == \"alarm_state\")].datatype").value("INTEGER"))
              .andExpect(jsonPath("$.readings[0].fields[?(@.name == \"product_id\")].value").value("Some Product ID"))
              .andExpect(jsonPath("$.readings[0].fields[?(@.name == \"product_id\")].datatype").value("STRING"))
              .andRespond(MockRestResponseCreators.withSuccess());

        client.pushData(sampleIngressData());

        server.verify();
    }

    @Test
    void givenServerErrorWhenPushingDataItShouldRetryIt4Times_5TimesTotal_beforeGivingUp() {
        server.expect(ExpectedCount.times(5), requestTo("https://api.logreposit.local/v2/ingress/data"))
              .andExpect(method(HttpMethod.POST))
              .andRespond(MockRestResponseCreators.withServerError());

        final var started = System.currentTimeMillis();

        final var thrown = Assertions.catchThrowable(() -> client.pushData(sampleIngressData()));

        Assertions.assertThat(thrown.getMessage()).isEqualTo("500 Internal Server Error: [no body]");
        Assertions.assertThat(System.currentTimeMillis() - started).isBetween(2000L, 3500L);

        server.verify();
    }

    @Test
    public void givenClientErrorEntityUnprocessableWhenPushingDataItUpdatesTheDeviceIngressDefinition() {
        server.expect(ExpectedCount.times(1), requestTo("https://api.logreposit.local/v2/ingress/data"))
              .andExpect(method(HttpMethod.POST))
              .andRespond(MockRestResponseCreators.withStatus(HttpStatus.UNPROCESSABLE_ENTITY));

        server.expect(ExpectedCount.times(1), requestTo("https://api.logreposit.local/v2/ingress/definition"))
                                 .andExpect(method(HttpMethod.PUT))
                                 .andExpect(jsonPath("$.measurements").isArray())
                                 .andExpect(jsonPath("$.measurements.length()").value(1))
                                 .andExpect(jsonPath("$.measurements[0].name").value("data"))
                                 .andExpect(jsonPath("$.measurements[0].tags").isArray())
                                 .andExpect(jsonPath("$.measurements[0].tags.length()").value(1))
                                 .andExpect(jsonPath("$.measurements[0].tags[0]").value("device_address"))
                                 .andExpect(jsonPath("$.measurements[0].fields").isArray())
                                 .andExpect(jsonPath("$.measurements[0].fields.length()").value(3))
                                 .andExpect(jsonPath("$.measurements[0].fields[0].name").value("battery_voltage"))
                                 .andExpect(jsonPath("$.measurements[0].fields[0].datatype").value(DataType.FLOAT.toString()))
                                 .andExpect(jsonPath("$.measurements[0].fields[0].description").value("Battery Voltage [V]"))
                                 .andExpect(jsonPath("$.measurements[0].fields[1].name").value("alarm_state"))
                                 .andExpect(jsonPath("$.measurements[0].fields[1].datatype").value(DataType.INTEGER.toString()))
                                 .andExpect(jsonPath("$.measurements[0].fields[1].description").value("Alarm State [ON/OFF]"))
                                 .andExpect(jsonPath("$.measurements[0].fields[2].name").value("product_id"))
                                 .andExpect(jsonPath("$.measurements[0].fields[2].datatype").value(DataType.STRING.toString()))
                                 .andExpect(jsonPath("$.measurements[0].fields[2].description").value("Product ID [str]"))
              .andRespond(MockRestResponseCreators.withSuccess());

        when(ingressDefinitionProvider.getIngressDefinition()).thenReturn(sampleIngressDefinition());

        client.pushData(sampleIngressData());

        server.verify();
    }

    private IngressData sampleIngressData() {
        final var batteryVoltageField = new FloatField("battery_voltage", 24.525);
        final var alarmStateField = new IntegerField("alarm_state", 1L);
        final var productIdField = new StringField("product_id", "Some Product ID");

        return new IngressData(List.of(new Reading(Instant.now(), "data", List.of(new Tag("device_address", "1")), List.of(batteryVoltageField, alarmStateField, productIdField))));
    }

    private IngressDefinition sampleIngressDefinition() {
        final var batteryVoltageField = new FieldDefinition("battery_voltage", DataType.FLOAT, "Battery Voltage [V]");
        final var alarmStateField = new FieldDefinition("alarm_state", DataType.INTEGER, "Alarm State [ON/OFF]");
        final var productIdField = new FieldDefinition("product_id", DataType.STRING, "Product ID [str]");

        return new IngressDefinition(List.of(new MeasurementDefinition("data", Set.of("device_address"), List.of(batteryVoltageField, alarmStateField, productIdField))));
    }
}
