package ru.yandex.practicum.processor.sensor;

import ru.yandex.practicum.LightSensorEvent;
import ru.yandex.practicum.SensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

public class LightSensorSnapshot implements SensorSnapshot {
    @Override
    public SensorEvent createSensorSnapshot(SensorStateAvro stateAvro, SensorsSnapshotAvro record, String str) {
        LightSensorEvent lightSensorEvent = new LightSensorEvent();
        lightSensorEvent.setHubId(record.getHubId());
        lightSensorEvent.setId(str);
        lightSensorEvent.setTimestamp(stateAvro.getTimestamp());
        lightSensorEvent.setLinkQuality(((LightSensorAvro) stateAvro.getData()).getLinkQuality());
        lightSensorEvent.setLuminosity(((LightSensorAvro) stateAvro.getData()).getLuminosity());
        return lightSensorEvent;
    }
}
