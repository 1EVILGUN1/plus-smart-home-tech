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
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.config.KafkaTopicsConfig;
import ru.yandex.practicum.kafka.config.SensorKafkaConfig;
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
    private static final Duration CONSUME_TIMEOUT = Duration.ofMillis(1000);

    private final KafkaTopicsConfig kafkaTopicsConfig;
    private final SensorKafkaConfig kafkaConfig;

    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
    private final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();

    public void start() {
        Properties consumerProperties = getConsumerProperties();
        try (KafkaConsumer<Void, SensorEventAvro> consumer = new KafkaConsumer<>(consumerProperties)) {
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
            consumer.subscribe(List.of(kafkaTopicsConfig.getSensors()));

            while (true) {
                processMessages(consumer);
            }
        } catch (WakeupException ignored) {
            log.info("Consumer shutdown triggered.");
        } catch (Exception e) {
            log.error("Error while processing sensor events", e);
        }
    }

    private void processMessages(KafkaConsumer<Void, SensorEventAvro> consumer) {
        ConsumerRecords<Void, SensorEventAvro> records = consumer.poll(CONSUME_TIMEOUT);
        int recordCount = 0;

        for (ConsumerRecord<Void, SensorEventAvro> record : records) {
            log.info("Received message from topic: {}", kafkaTopicsConfig.getSensors());
            SensorsSnapshotAvro snapshot = updateSnapshot(record);
            if (snapshot != null) {
                sendSnapshot(snapshot);
            }
            trackOffset(record, ++recordCount, consumer);
        }

        consumer.commitAsync();
    }

    private SensorsSnapshotAvro updateSnapshot(ConsumerRecord<Void, SensorEventAvro> record) {
        String hubId = record.value().getHubId();
        SensorsSnapshotAvro snapshot = snapshots.getOrDefault(hubId, createNewSnapshot(record));

        SensorStateAvro newState = new SensorStateAvro(
                Instant.ofEpochSecond(record.timestamp()),
                record.value().getPayload()
        );

        Map<String, SensorStateAvro> sensorStates = snapshot.getSensorsState();
        String sensorId = record.value().getId();

        if (!sensorStates.containsKey(sensorId) || !sensorStates.get(sensorId).equals(newState)) {
            sensorStates.put(sensorId, newState);
            snapshot.setTimestamp(Instant.ofEpochSecond(record.timestamp()));
            snapshots.put(hubId, snapshot);
            return snapshot;
        }

        return null;
    }

    private SensorsSnapshotAvro createNewSnapshot(ConsumerRecord<Void, SensorEventAvro> record) {
        SensorsSnapshotAvro snapshot = new SensorsSnapshotAvro();
        snapshot.setHubId(record.value().getHubId());
        snapshot.setTimestamp(Instant.ofEpochSecond(record.timestamp()));
        snapshot.setSensorsState(new HashMap<>());
        return snapshot;
    }

    private void sendSnapshot(SensorsSnapshotAvro snapshot) {
        Properties producerProperties = getProducerProperties();
        try (Producer<String, SensorsSnapshotAvro> producer = new KafkaProducer<>(producerProperties)) {
            ProducerRecord<String, SensorsSnapshotAvro> record = new ProducerRecord<>(
                    kafkaTopicsConfig.getSnapshots(), snapshot
            );
            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    log.error("Error while sending snapshot: {}", snapshot, exception);
                } else {
                    log.info("Snapshot sent successfully to topic: {}", kafkaTopicsConfig.getSnapshots());
                }
            });
        }
    }

    private void trackOffset(ConsumerRecord<Void, SensorEventAvro> record, int count, KafkaConsumer<Void, SensorEventAvro> consumer) {
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1)
        );

        if (OffsetCommitCondition.shouldCommit(count)) {
            consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if (exception != null) {
                    log.warn("Error while committing offsets: {}", offsets, exception);
                }
            });
        }
    }

    private Properties getConsumerProperties() {
        Properties config = new Properties();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getConsumer().getBootstrapServers());
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaConfig.getConsumer().getKeyDeserializer());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, kafkaConfig.getConsumer().getValueDeserializer());
        config.put(ConsumerConfig.CLIENT_ID_CONFIG, kafkaConfig.getConsumer().getClientId());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConfig.getConsumer().getGroupId());
        return config;
    }

    private Properties getProducerProperties() {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getProducer().getBootstrapServers());
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaConfig.getProducer().getKeySerializer());
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaConfig.getProducer().getValueSerializer());
        return config;
    }
}
