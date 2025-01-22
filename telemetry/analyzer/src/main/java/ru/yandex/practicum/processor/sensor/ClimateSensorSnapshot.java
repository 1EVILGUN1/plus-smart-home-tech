package ru.yandex.practicum.processor.sensor;

import ru.yandex.practicum.ClimateSensorEvent;
import ru.yandex.practicum.SensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

public class ClimateSensorSnapshot implements SensorSnapshot {
    @Override
    public SensorEvent createSensorSnapshot(SensorStateAvro stateAvro, SensorsSnapshotAvro record, String str) {
        ClimateSensorEvent climateSensorEvent = new ClimateSensorEvent();
        climateSensorEvent.setHubId(record.getHubId());
        climateSensorEvent.setId(str);
        climateSensorEvent.setTimestamp(stateAvro.getTimestamp());
        climateSensorEvent.setTemperatureC(((ClimateSensorAvro) stateAvro.getData()).getTemperatureC());
        climateSensorEvent.setHumidity(((ClimateSensorAvro) stateAvro.getData()).getHumidity());
        climateSensorEvent.setCo2Level(((ClimateSensorAvro) stateAvro.getData()).getCo2Level());
        return climateSensorEvent;
    }
}
