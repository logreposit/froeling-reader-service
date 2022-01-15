package com.logreposit.froelingreaderservice.metrics.logreposit;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.FunctionTimer;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.step.StepMeterRegistry;
import io.micrometer.core.instrument.util.DoubleFormat;
import io.micrometer.core.instrument.util.MeterPartition;
import io.micrometer.core.instrument.util.NamedThreadFactory;
import io.micrometer.core.instrument.util.StringUtils;
import io.micrometer.core.ipc.http.HttpSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class LogrepositMeterRegistry extends StepMeterRegistry
{
    private final LogrepositConfig config;
    private final HttpSender httpClient;
    private final Logger logger = LoggerFactory.getLogger(LogrepositMeterRegistry.class);
    private boolean databaseExists = false;

    public LogrepositMeterRegistry(LogrepositConfig config, Clock clock, ThreadFactory threadFactory, HttpSender httpClient) {
        super(config, clock);
        config().namingConvention(new LogrepositNamingConvention());
        this.config = config;
        this.httpClient = httpClient;
        start(threadFactory);
    }

    @Override
    public void start(ThreadFactory threadFactory) {
        super.start(threadFactory);

        if (config.enabled()) {
            logger.info("Using Logreposit API with config measurement={} to write metrics", config.measurement());
        }
    }

//    public static Builder builder(LogrepositConfig config) {
//        return new Builder(config);
//    }

    private void createDatabaseIfNecessary() {
        // TODO DoM: maybe we need this for for schema creation?
    }

    @Override
    protected void publish() {
        createDatabaseIfNecessary();

        try {
            String influxEndpoint = "config.apiVersion().writeEndpoint(config)";

            for (List<Meter> batch : MeterPartition.partition(this, config.batchSize())) {
//                HttpSender.Request.Builder requestBuilder = httpClient
//                        .post(influxEndpoint)
//                        .withBasicAuthentication("config.username()", "config.password()");

                var plainText = batch.stream()
                                     .flatMap(m -> m.match(
                                             gauge -> writeGauge(gauge.getId(), gauge.value()),
                                             counter -> writeCounter(counter.getId(), counter.count()),
                                             this::writeTimer,
                                             this::writeSummary,
                                             this::writeLongTaskTimer,
                                             gauge -> writeGauge(gauge.getId(), gauge.value(getBaseTimeUnit())),
                                             counter -> writeCounter(counter.getId(), counter.count()),
                                             this::writeFunctionTimer,
                                             this::writeMeter))
                                     .collect(joining("\n"));

                logger.info("DoM: Would send the following metrics:\n{}", plainText);

//                requestBuilder
//                        .withPlainText(plainText)
//                        .send()
//                        .onSuccess(response -> {
//                            logger.debug("successfully sent {} metrics to InfluxDB.", batch.size());
//                            databaseExists = true;
//                        })
//                        .onError(response -> logger.error("failed to send metrics to influx: {}", response.body()));

                if (!config.enabled()) {
                    throw new MalformedURLException(); // TODO: just to make compiler happy
                }
            }
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Malformed InfluxDB publishing endpoint, see '" + config.prefix() + ".uri'", e);
        } catch (Throwable e) {
            logger.error("failed to send metrics to influx", e);
        }
    }

    // VisibleForTesting
    Stream<String> writeMeter(Meter m) {
        List<Field> fields = new ArrayList<>();
        for (Measurement measurement : m.measure()) {
            double value = measurement.getValue();
            if (!Double.isFinite(value)) {
                continue;
            }
            String fieldKey = measurement.getStatistic().getTagValueRepresentation()
                                         .replaceAll("(.)(\\p{Upper})", "$1_$2").toLowerCase();
            fields.add(new Field(fieldKey, value));
        }
        if (fields.isEmpty()) {
            return Stream.empty();
        }
        Meter.Id id = m.getId();
        return Stream.of(influxLineProtocol(id, id.getType().name().toLowerCase(), fields.stream()));
    }

    private Stream<String> writeLongTaskTimer(LongTaskTimer timer) {
        Stream<Field> fields = Stream.of(
                new Field("active_tasks", timer.activeTasks()),
                new Field("duration", timer.duration(getBaseTimeUnit()))
        );
        return Stream.of(influxLineProtocol(timer.getId(), "long_task_timer", fields));
    }

    // VisibleForTesting
    Stream<String> writeCounter(Meter.Id id, double count) {
        if (Double.isFinite(count)) {
            return Stream.of(influxLineProtocol(id, "counter", Stream.of(new Field("value", count))));
        }
        return Stream.empty();
    }

    // VisibleForTesting
    Stream<String> writeGauge(Meter.Id id, Double value) {
        if (Double.isFinite(value)) {
            return Stream.of(influxLineProtocol(id, "gauge", Stream.of(new Field("value", value))));
        }
        return Stream.empty();
    }

    // VisibleForTesting
    Stream<String> writeFunctionTimer(FunctionTimer timer) {
        double sum = timer.totalTime(getBaseTimeUnit());
        if (Double.isFinite(sum)) {
            Stream.Builder<Field> builder = Stream.builder();
            builder.add(new Field("sum", sum));
            builder.add(new Field("count", timer.count()));
            double mean = timer.mean(getBaseTimeUnit());
            if (Double.isFinite(mean)) {
                builder.add(new Field("mean", mean));
            }
            return Stream.of(influxLineProtocol(timer.getId(), "histogram", builder.build()));
        }
        return Stream.empty();
    }

    private Stream<String> writeTimer(Timer timer) {
        final Stream<Field> fields = Stream.of(
                new Field("sum", timer.totalTime(getBaseTimeUnit())),
                new Field("count", timer.count()),
                new Field("mean", timer.mean(getBaseTimeUnit())),
                new Field("upper", timer.max(getBaseTimeUnit()))
        );

        return Stream.of(influxLineProtocol(timer.getId(), "histogram", fields));
    }

    private Stream<String> writeSummary(DistributionSummary summary) {
        final Stream<Field> fields = Stream.of(
                new Field("sum", summary.totalAmount()),
                new Field("count", summary.count()),
                new Field("mean", summary.mean()),
                new Field("upper", summary.max())
        );

        return Stream.of(influxLineProtocol(summary.getId(), "histogram", fields));
    }

    private String influxLineProtocol(Meter.Id id, String metricType, Stream<Field> fields) {
        String tags = getConventionTags(id).stream()
                                           .filter(t -> StringUtils.isNotBlank(t.getValue()))
                                           .map(t -> "," + t.getKey() + "=" + t.getValue())
                                           .collect(joining(""));

        return getConventionName(id)
                + tags + ",metric_type=" + metricType + " "
                + fields.map(Field::toString).collect(joining(","))
                + " " + clock.wallTime();
    }

    @Override
    protected final TimeUnit getBaseTimeUnit() {
        return TimeUnit.MILLISECONDS;
    }

//    public static class Builder {
//        private final LogrepositConfig config;
//
//        private Clock clock = Clock.SYSTEM;
//        private ThreadFactory threadFactory = DEFAULT_THREAD_FACTORY;
//        private HttpSender httpClient;
//
//        @SuppressWarnings("deprecation")
//        Builder(LogrepositConfig config) {
//            this.config = config;
//            this.httpClient = new HttpUrlConnectionSender(config.connectTimeout(), config.readTimeout());
//        }
//
//        public Builder clock(Clock clock) {
//            this.clock = clock;
//            return this;
//        }
//
//        public Builder threadFactory(ThreadFactory threadFactory) {
//            this.threadFactory = threadFactory;
//            return this;
//        }
//
//        public Builder httpClient(HttpSender httpClient) {
//            this.httpClient = httpClient;
//            return this;
//        }
//
//        public LogrepositMeterRegistry build() {
//            return new LogrepositMeterRegistry(config, clock, threadFactory, httpClient);
//        }
//    }

    static class Field {
        final String key;
        final double value;

        Field(String key, double value) {
            // `time` cannot be a field key or tag key
            if (key.equals("time")) {
                throw new IllegalArgumentException("'time' is an invalid field key in InfluxDB");
            }
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return key + "=" + DoubleFormat.decimalOrNan(value);
        }
    }
}
