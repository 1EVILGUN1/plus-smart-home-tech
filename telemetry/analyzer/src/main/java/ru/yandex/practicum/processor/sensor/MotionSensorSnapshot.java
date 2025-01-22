package ru.yandex.practicum.processor.sensor;

import ru.yandex.practicum.MotionSensorEvent;
import ru.yandex.practicum.SensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

public class MotionSensorSnapshot implements SensorSnapshot {
    @Override
    public SensorEvent createSensorSnapshot(SensorStateAvro stateAvro, SensorsSnapshotAvro record, String str) {
        MotionSensorEvent motionSensorEvent = new MotionSensorEvent();
        motionSensorEvent.setHubId(record.getHubId());
        motionSensorEvent.setId(str);
        motionSensorEvent.setTimestamp(stateAvro.getTimestamp());
        motionSensorEvent.setMotion(((MotionSensorAvro) stateAvro.getData()).getMotion());
        motionSensorEvent.setLinkQuality(((MotionSensorAvro) stateAvro.getData()).getLinkQuality());
        motionSensorEvent.setVoltage(((MotionSensorAvro) stateAvro.getData()).getVoltage());
        return motionSensorEvent;
    }
}
