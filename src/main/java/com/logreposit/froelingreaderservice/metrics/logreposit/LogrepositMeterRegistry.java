package com.logreposit.froelingreaderservice.metrics.logreposit;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.FunctionTimer;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.TimeGauge;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.step.StepMeterRegistry;
import io.micrometer.core.instrument.util.DoubleFormat;
import io.micrometer.core.instrument.util.MeterPartition;
import io.micrometer.core.instrument.util.StringUtils;
import io.micrometer.core.ipc.http.HttpSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class LogrepositMeterRegistry extends StepMeterRegistry
{
    private final LogrepositConfig config;
    private final Logger logger = LoggerFactory.getLogger(LogrepositMeterRegistry.class);

    public LogrepositMeterRegistry(LogrepositConfig config, Clock clock, ThreadFactory threadFactory, HttpSender httpClient) {
        super(config, clock);

        config().namingConvention(new LogrepositNamingConvention());
        this.config = config;
        start(threadFactory);
    }

    @Override
    public void start(ThreadFactory threadFactory) {
        super.start(threadFactory);
    }

    private void createDatabaseIfNecessary() {
        // TODO DoM: maybe we need this for for schema creation?
    }

    @Override
    protected void publish() {
        createDatabaseIfNecessary();

        try {
            for (List<Meter> batch : MeterPartition.partition(this, config.batchSize())) {
                var plainText = batch.stream()
                                     .flatMap(m -> m.match(
                                             this::writeGauge,
                                             this::writeCounter,
                                             this::writeTimer,
                                             this::writeSummary,
                                             this::writeLongTaskTimer,
                                             this::writeTimeGauge,
                                             this::writeCounter,
                                             this::writeFunctionTimer,
                                             this::writeMeter))
                                     .collect(joining("\n"));

                logger.info("DoM: Would send the following metrics:\n{}", plainText);}
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

    Stream<String> writeCounter(FunctionCounter functionCounter) {
        return writeCounter(functionCounter.getId(), functionCounter.count());
    }

    // VisibleForTesting
    Stream<String> writeCounter(Counter counter) {
        return writeCounter(counter.getId(), counter.count());
    }

    private Stream<String> writeCounter(Meter.Id id, double count) {
        if (Double.isFinite(count)) {
            return Stream.of(influxLineProtocol(id, "counter", Stream.of(new Field("value", count))));
        }

        return Stream.empty();
    }

    // VisibleForTesting
    Stream<String> writeGauge(Gauge gauge) {
        return writeGauge(gauge.getId(), gauge.value());
    }

    Stream<String> writeTimeGauge(TimeGauge timeGauge) {
        return writeGauge(timeGauge.getId(), timeGauge.value(getBaseTimeUnit()));
    }

    // VisibleForTesting
    private Stream<String> writeGauge(Meter.Id id, double value) {
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
