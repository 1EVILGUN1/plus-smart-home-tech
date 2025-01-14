package ru.yandex.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.VoidDeserializer;
import org.apache.kafka.common.serialization.VoidSerializer;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.serialize.SensorEventDeserializer;
import ru.yandex.practicum.serialize.SensorsSnapshotSrializer;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {

    private static final List<String> topics = List.of("telemetry.sensors.v1");
    private static final Duration consume_attempt_timeout = Duration.ofMillis(1000);
    private static final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    private static final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();

    private static void manageOffsets(ConsumerRecord<Void, SensorEventAvro> record, int count, KafkaConsumer<Void, SensorEventAvro> consumer) {
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1)
        );

        if (count % 10 == 0) {
            consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if (exception != null) {
                    log.warn("Ошибка во время фиксации оффсетов: {}", offsets, exception);
                }
            });
        }
    }

    public void start() {
        Properties config = getPropertiesConsumerSensor();
        KafkaConsumer<Void, SensorEventAvro> consumer = new KafkaConsumer<>(config);
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

        try {
            consumer.subscribe(topics);
            while (true) {

                ConsumerRecords<Void, SensorEventAvro> records = consumer.poll(consume_attempt_timeout);

                int count = 0;
                for (ConsumerRecord<Void, SensorEventAvro> record : records) {
                    System.out.println("Получено сообщение. topic: telemetry.sensors.v1");

                    Optional<SensorsSnapshotAvro> result = Optional.empty();
                    if (snapshots.containsKey(record.value().getHubId())) {
                        SensorsSnapshotAvro snapshot = snapshots.get(record.value().getHubId());
                        SensorStateAvro stateAvro = new SensorStateAvro(Instant.ofEpochSecond(record.timestamp()), record.value().getPayload());
                        if (snapshot.getSensorsState().containsKey(record.value().getId())) {
                            if (!snapshot.getSensorsState().get(record.value().getId()).equals(stateAvro)) {
                                snapshot.getSensorsState().put(record.value().getId(), stateAvro);
                                result = Optional.of(snapshot);
                            }
                        } else {
                            snapshot.getSensorsState().put(record.value().getId(), stateAvro);
                            result = Optional.of(snapshot);
                        }

                    } else {
                        SensorsSnapshotAvro snapshot = new SensorsSnapshotAvro();
                        snapshot.setHubId(record.value().getHubId());
                        snapshot.setTimestamp(Instant.ofEpochSecond(record.timestamp()));
                        SensorStateAvro stateAvro = new SensorStateAvro(Instant.ofEpochSecond(record.timestamp()), record.value().getPayload());

                        snapshot.setSensorsState(new HashMap<>());
                        snapshot.getSensorsState().put(record.value().getId(), stateAvro);
                        snapshots.put(record.value().getHubId(), snapshot);
                        result = Optional.of(snapshot);
                    }

                    if (result.isPresent()) {
                        Properties properties = getPropertiesProducerSensor();
                        Producer<String, SensorsSnapshotAvro> producer = new KafkaProducer<>(properties);
                        String snapshotTopic = "telemetry.snapshots.v1";
                        ProducerRecord<String, SensorsSnapshotAvro> snapshotRecord = new ProducerRecord<>(snapshotTopic, result.get());
                        producer.send(snapshotRecord);
                        producer.close();
                    }
                    manageOffsets(record, count, consumer);
                    count++;
                }
                consumer.commitAsync();
            }


        } catch (
                WakeupException ignored) {
        } catch (
                Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {

            try {
                consumer.commitSync(currentOffsets);

            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
            }
        }
    }

    private Properties getPropertiesConsumerSensor() {
        Properties config = new Properties();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, VoidDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorEventDeserializer.class);
        config.put(ConsumerConfig.CLIENT_ID_CONFIG, "SomeConsumer");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "some.group.id");
        return config;
    }

    private Properties getPropertiesProducerSensor() {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, VoidSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, SensorsSnapshotSrializer.class);
        return config;
    }
}
