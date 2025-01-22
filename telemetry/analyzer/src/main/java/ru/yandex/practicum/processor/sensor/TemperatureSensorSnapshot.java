package ru.yandex.practicum.processor.sensor;

import ru.yandex.practicum.SensorEvent;
import ru.yandex.practicum.TemperatureSensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

public class TemperatureSensorSnapshot implements SensorSnapshot {
    @Override
    public SensorEvent createSensorSnapshot(SensorStateAvro stateAvro, SensorsSnapshotAvro record, String str) {
        TemperatureSensorEvent temperatureSensorEvent = new TemperatureSensorEvent();
        temperatureSensorEvent.setHubId(record.getHubId());
        temperatureSensorEvent.setId(str);
        temperatureSensorEvent.setTimestamp(stateAvro.getTimestamp());
        temperatureSensorEvent.setTemperatureC(((TemperatureSensorAvro) stateAvro.getData()).getTemperatureC());
        temperatureSensorEvent.setTemperatureF(((TemperatureSensorAvro) stateAvro.getData()).getTemperatureF());
        return temperatureSensorEvent;
    }
}
