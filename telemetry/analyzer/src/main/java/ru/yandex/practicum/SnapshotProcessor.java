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
import ru.yandex.practicum.service.SnapshotService;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor {
    private static final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
    private final SnapshotService service;
    private final SnapshotConsumerConfig snapshotConsumerConfig;

    private static void manageOffsets(ConsumerRecord<Void, SensorsSnapshotAvro> record, int count, KafkaConsumer<Void, SensorsSnapshotAvro> consumer) {
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

                // Периодичное получение записей из Kafka
                ConsumerRecords<Void, SensorsSnapshotAvro> records = consumer.poll(snapshotConsumerConfig.getConsumer().getConsumeAttemptTimeout());
                int count = 0;
                for (ConsumerRecord<Void, SensorsSnapshotAvro> record : records) {

                    log.info("Получено сообщение. topic: telemetry.snapshots.v1 {}\n", record.value());

                    // Обработка данных события
                    SensorEventSnapshot snapshot = new SensorEventSnapshot();
                    snapshot.setHubId(record.value().getHubId());
                    snapshot.setTimestapm(record.value().getTimestamp());
                    Map<String, SensorState> stateMap = new HashMap<>();
                    Set<String> keys = record.value().getSensorsState().keySet();
                    for (String str : keys) {
                        SensorStateAvro stateAvro = record.value().getSensorsState().get(str);
                        SensorState state = new SensorState();
                        state.setTimestamp(stateAvro.getTimestamp());

                        if (stateAvro.getData() instanceof MotionSensorAvro) {
                            MotionSensorEvent motionSensorEvent = new MotionSensorEvent();
                            motionSensorEvent.setHubId(record.value().getHubId());
                            motionSensorEvent.setId(str);
                            motionSensorEvent.setTimestamp(stateAvro.getTimestamp());
                            motionSensorEvent.setMotion(((MotionSensorAvro) stateAvro.getData()).getMotion());
                            motionSensorEvent.setLinkQuality(((MotionSensorAvro) stateAvro.getData()).getLinkQuality());
                            motionSensorEvent.setVoltage(((MotionSensorAvro) stateAvro.getData()).getVoltage());
                            state.setData(motionSensorEvent);
                        } else if (stateAvro.getData() instanceof TemperatureSensorAvro) {
                            TemperatureSensorEvent temperatureSensorEvent = new TemperatureSensorEvent();
                            temperatureSensorEvent.setHubId(record.value().getHubId());
                            temperatureSensorEvent.setId(str);
                            temperatureSensorEvent.setTimestamp(stateAvro.getTimestamp());
                            temperatureSensorEvent.setTemperatureC(((TemperatureSensorAvro) stateAvro.getData()).getTemperatureC());
                            temperatureSensorEvent.setTemperatureF(((TemperatureSensorAvro) stateAvro.getData()).getTemperatureF());
                            state.setData(temperatureSensorEvent);
                        } else if (stateAvro.getData() instanceof LightSensorAvro) {
                            LightSensorEvent lightSensorEvent = new LightSensorEvent();
                            lightSensorEvent.setHubId(record.value().getHubId());
                            lightSensorEvent.setId(str);
                            lightSensorEvent.setTimestamp(stateAvro.getTimestamp());
                            lightSensorEvent.setLinkQuality(((LightSensorAvro) stateAvro.getData()).getLinkQuality());
                            lightSensorEvent.setLuminosity(((LightSensorAvro) stateAvro.getData()).getLuminosity());
                            state.setData(lightSensorEvent);
                        } else if (stateAvro.getData() instanceof ClimateSensorAvro) {
                            ClimateSensorEvent climateSensorEvent = new ClimateSensorEvent();
                            climateSensorEvent.setHubId(record.value().getHubId());
                            climateSensorEvent.setId(str);
                            climateSensorEvent.setTimestamp(stateAvro.getTimestamp());
                            climateSensorEvent.setTemperatureC(((ClimateSensorAvro) stateAvro.getData()).getTemperatureC());
                            climateSensorEvent.setHumidity(((ClimateSensorAvro) stateAvro.getData()).getHumidity());
                            climateSensorEvent.setCo2Level(((ClimateSensorAvro) stateAvro.getData()).getCo2Level());
                            state.setData(climateSensorEvent);
                        } else if (stateAvro.getData() instanceof SwitchSensorAvro) {
                            SwitchSensorEvent switchSensorEvent = new SwitchSensorEvent();
                            switchSensorEvent.setHubId(record.value().getHubId());
                            switchSensorEvent.setId(str);
                            switchSensorEvent.setTimestamp(stateAvro.getTimestamp());
                            switchSensorEvent.setState(((SwitchSensorAvro) stateAvro.getData()).getState());
                            state.setData(switchSensorEvent);
                        }

                        stateMap.put(str, state);
                    }
                    snapshot.setSensorsState(stateMap);

                    // Обработка снимка
                    service.processingSnapshot(snapshot);

                    manageOffsets(record, count, consumer);
                    count++;
                }

                consumer.commitAsync();

            }

        } catch (WakeupException ignored) {
            // Игнорируем исключение при остановке
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

    // Получаем конфигурацию Kafka из свойств
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



