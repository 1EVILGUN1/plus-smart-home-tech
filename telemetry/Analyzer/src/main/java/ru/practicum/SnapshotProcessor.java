package ru.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.VoidDeserializer;
import org.springframework.stereotype.Component;
import ru.practicum.model.SensorEventSnapshot;
import ru.practicum.model.SensorState;
import ru.practicum.serialize.SensorsSnapshotDeserializer;
import ru.practicum.service.SnapshotService;
import ru.yandex.practicum.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Duration;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor {
    private static final List<String> topics = List.of("telemetry.snapshots.v1");
    private static final Duration consume_attempt_timeout = Duration.ofMillis(1000);
    private static final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    private final SnapshotService service;

    public void start() {
        Properties config = getPropertiesConsumerSnapshot();
        KafkaConsumer<Void, SensorsSnapshotAvro> consumer = new KafkaConsumer<>(config);

        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
        try {

            consumer.subscribe(topics);

            while (true) {

                ConsumerRecords<Void, SensorsSnapshotAvro> records = consumer.poll(consume_attempt_timeout);
                int count = 0;
                for (ConsumerRecord<Void, SensorsSnapshotAvro> record : records) {

                    System.out.println("Получено сообщение. topic: telemetry.snapshots.v1");
                    System.out.println(record.value());

                    SensorEventSnapshot snapshot = new SensorEventSnapshot();
                    snapshot.setHubId(record.value().getHubId());
                    snapshot.setTimestapm(record.value().getTimestamp());
                    Map<String, SensorState> stateMap = new HashMap<>();
                    Set<String> keys = record.value().getSensorsState().keySet();
                    for (String str : keys) {
                        SensorStateAvro stateAvro = record.value().getSensorsState().get(str);
                        SensorState state = new SensorState();
                        state.setTimestamp(stateAvro.getTimestamp());

                        switch (stateAvro.getData().getClass().getSimpleName()) {
                            case "MotionSensorAvro":
                                MotionSensorEvent motionSensorEvent = new MotionSensorEvent();
                                motionSensorEvent.setHubId(record.value().getHubId());
                                motionSensorEvent.setId(str);
                                motionSensorEvent.setTimestamp(stateAvro.getTimestamp());
                                motionSensorEvent.setMotion(((MotionSensorAvro) stateAvro.getData()).getMotion());
                                motionSensorEvent.setLinkQuality(((MotionSensorAvro) stateAvro.getData()).getLinkQuality());
                                motionSensorEvent.setVoltage(((MotionSensorAvro) stateAvro.getData()).getVoltage());

                                state.setData(motionSensorEvent);
                                break;
                            case "TemperatureSensorAvro":
                                TemperatureSensorEvent temperatureSensorEvent = new TemperatureSensorEvent();
                                temperatureSensorEvent.setHubId(record.value().getHubId());
                                temperatureSensorEvent.setId(str);
                                temperatureSensorEvent.setTimestamp(stateAvro.getTimestamp());
                                temperatureSensorEvent.setTemperatureC(((TemperatureSensorAvro) stateAvro.getData()).getTemperatureC());
                                temperatureSensorEvent.setTemperatureF(((TemperatureSensorAvro) stateAvro.getData()).getTemperatureF());

                                state.setData(temperatureSensorEvent);
                                break;
                            case "LightSensorAvro":
                                LightSensorEvent lightSensorEvent = new LightSensorEvent();
                                lightSensorEvent.setHubId(record.value().getHubId());
                                lightSensorEvent.setId(str);
                                lightSensorEvent.setTimestamp(stateAvro.getTimestamp());
                                lightSensorEvent.setLinkQuality(((LightSensorAvro) stateAvro.getData()).getLinkQuality());
                                lightSensorEvent.setLuminosity(((LightSensorAvro) stateAvro.getData()).getLuminosity());

                                state.setData(lightSensorEvent);
                                break;
                            case "ClimateSensorAvro":
                                ClimateSensorEvent climateSensorEvent = new ClimateSensorEvent();
                                climateSensorEvent.setHubId(record.value().getHubId());
                                climateSensorEvent.setId(str);
                                climateSensorEvent.setTimestamp(stateAvro.getTimestamp());
                                climateSensorEvent.setTemperatureC(((ClimateSensorAvro) stateAvro.getData()).getTemperatureC());
                                climateSensorEvent.setHumidity(((ClimateSensorAvro) stateAvro.getData()).getHumidity());
                                climateSensorEvent.setCo2Level(((ClimateSensorAvro) stateAvro.getData()).getCo2Level());

                                state.setData(climateSensorEvent);
                                break;
                            case "SwitchSensorAvro":
                                SwitchSensorEvent switchSensorEvent = new SwitchSensorEvent();
                                switchSensorEvent.setHubId(record.value().getHubId());
                                switchSensorEvent.setId(str);
                                switchSensorEvent.setTimestamp(stateAvro.getTimestamp());
                                switchSensorEvent.setState(((SwitchSensorAvro)stateAvro.getData()).getState());

                                state.setData(switchSensorEvent);
                                break;
                        }

                        stateMap.put(str, state);
                    }
                    snapshot.setSensorsState(stateMap);
                    service.processingSnapshot(snapshot);

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

    private Properties getPropertiesConsumerSnapshot() {
        Properties config = new Properties();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, VoidDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorsSnapshotDeserializer.class);
        config.put(ConsumerConfig.CLIENT_ID_CONFIG, "SomeConsumer1");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "some.group.id1");
        return config;
    }

    private static void manageOffsets(ConsumerRecord<Void, SensorsSnapshotAvro> record, int count, KafkaConsumer<Void, SensorsSnapshotAvro> consumer) {
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
}


