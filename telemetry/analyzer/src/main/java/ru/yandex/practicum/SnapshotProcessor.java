package ru.yandex.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.config.SnapshotConsumerConfig;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.SensorEventSnapshot;
import ru.yandex.practicum.model.SensorState;
import ru.yandex.practicum.processor.sensor.*;
import ru.yandex.practicum.service.SnapshotService;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor {
    private static final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
    private final SnapshotService service;
    private final SnapshotConsumerConfig snapshotConsumerConfig;
    private final SensorSnapshot sensorSnapshot;

    private static void managerOffsets(ConsumerRecord<Void, SensorsSnapshotAvro> record, int count, KafkaConsumer<Void, SensorsSnapshotAvro> consumer) {
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1)
        );

        if (OffsetCommitCondition.shouldCommit(count)) {
            consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if (exception != null) {
                    log.warn("Ошибка во время фиксации оффсетов: {}", offsets, exception);
                }
            });
        }
    }

    public void start() {
        Properties config = getPropertiesConsumerSnapshot();
        KafkaConsumer<Void, SensorsSnapshotAvro> consumer = new KafkaConsumer<>(config);

        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
        try {

            // Подписываемся на топики, указанные в конфигурации
            consumer.subscribe(snapshotConsumerConfig.getTopics());

            while (true) {
                ConsumerRecords<Void, SensorsSnapshotAvro> records = consumer.poll(snapshotConsumerConfig.getConsumer().getConsumeAttemptTimeout());
                int count = 0;
                for (ConsumerRecord<Void, SensorsSnapshotAvro> record : records) {
                    log.info("Получено сообщение. topic: telemetry.snapshots.v1 {}\n", record.value());
                    SensorEventSnapshot snapshot = new SensorEventSnapshot();
                    snapshot.setHubId(record.value().getHubId());
                    snapshot.setTimestapm(record.value().getTimestamp());
                    Map<String, SensorState> stateMap = new HashMap<>();
                    Set<String> keys = record.value().getSensorsState().keySet();
                    for (String str : keys) {
                        SensorStateAvro stateAvro = record.value().getSensorsState().get(str);
                        SensorState state = new SensorState();
                        state.setTimestamp(stateAvro.getTimestamp());

                        Map<Class<?>, Function<SensorStateAvro, SensorEvent>> sensorEventMapping = Map.of(
                                MotionSensorAvro.class, avro ->
                                        new MotionSensorSnapshot().createSensorSnapshot(avro, record.value(), str),
                                TemperatureSensorAvro.class, avro ->
                                        new TemperatureSensorSnapshot().createSensorSnapshot(avro, record.value(), str),
                                LightSensorAvro.class, avro ->
                                        new LightSensorSnapshot().createSensorSnapshot(avro, record.value(), str),
                                ClimateSensorAvro.class, avro ->
                                        new ClimateSensorSnapshot().createSensorSnapshot(avro, record.value(), str),
                                SwitchSensorAvro.class, avro ->
                                        new SwitchSensorSnapshot().createSensorSnapshot(avro, record.value(), str)
                        );

                        sensorEventMapping.entrySet().stream()
                                .filter(entry -> entry.getKey().isInstance(stateAvro.getData()))
                                .findFirst()
                                .ifPresent(entry -> {
                                    SensorEvent sensorEvent = entry.getValue().apply(stateAvro);
                                    state.setData(sensorEvent);
                                });

                        stateMap.put(str, state);
                    }
                    snapshot.setSensorsState(stateMap);
                    service.processingSnapshot(snapshot);
                    managerOffsets(record, count, consumer);
                    count++;
                }

                consumer.commitAsync();

            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
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

    private Properties getPropertiesConsumerSnapshot() {
        Properties config = new Properties();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, snapshotConsumerConfig.getBootstrapServers());
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, snapshotConsumerConfig.getConsumer().getKeyDeserializer());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, snapshotConsumerConfig.getConsumer().getValueDeserializer());
        config.put(ConsumerConfig.CLIENT_ID_CONFIG, snapshotConsumerConfig.getClientId());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, snapshotConsumerConfig.getGroupId());
        return config;
    }
}



