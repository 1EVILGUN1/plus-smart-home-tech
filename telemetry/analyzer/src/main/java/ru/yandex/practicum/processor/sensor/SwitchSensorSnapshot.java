package ru.yandex.practicum.processor.sensor;

import ru.yandex.practicum.SensorEvent;
import ru.yandex.practicum.SwitchSensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;

public class SwitchSensorSnapshot implements SensorSnapshot {
    @Override
    public SensorEvent createSensorSnapshot(SensorStateAvro stateAvro, SensorsSnapshotAvro record, String str) {
        SwitchSensorEvent switchSensorEvent = new SwitchSensorEvent();
        switchSensorEvent.setHubId(record.getHubId());
        switchSensorEvent.setId(str);
        switchSensorEvent.setTimestamp(stateAvro.getTimestamp());
        switchSensorEvent.setState(((SwitchSensorAvro) stateAvro.getData()).getState());
        return switchSensorEvent;
    }
}
