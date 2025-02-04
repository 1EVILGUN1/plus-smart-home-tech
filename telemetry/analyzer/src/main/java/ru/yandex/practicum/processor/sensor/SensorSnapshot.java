package ru.yandex.practicum.processor.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.SensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

@Component
public interface SensorSnapshot {

    SensorEvent createSensorSnapshot(SensorStateAvro stateAvro, SensorsSnapshotAvro record, String str);
}
