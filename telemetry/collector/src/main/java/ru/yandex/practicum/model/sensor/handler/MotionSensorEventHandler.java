package ru.yandex.practicum.model.sensor.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.MotionSensorEvent;
import ru.yandex.practicum.grpc.telemetry.event.MotionSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.service.CollectorService;

import java.time.Instant;

import static ru.yandex.practicum.grpc.telemetry.event.SensorEventProto.PayloadCase.MOTION_SENSOR;

@Component
@RequiredArgsConstructor
public class MotionSensorEventHandler implements SensorEventHandler {
    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return MOTION_SENSOR;
    }

    private final CollectorService service;

    @Override
    public void handle(SensorEventProto event) {
        System.out.println("Получено событие датчика движения");
        // получаем данные датчика движения
        MotionSensorProto motionSensor = event.getMotionSensor();
        System.out.println("Наличие движения: " + motionSensor.getMotion());

        MotionSensorEvent sensorEvent = new MotionSensorEvent();
        sensorEvent.setId(event.getId());
        sensorEvent.setHubId(event.getHubId());
        sensorEvent.setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds()));
        sensorEvent.setMotion(event.getMotionSensor().getMotion());
        sensorEvent.setVoltage(event.getMotionSensor().getVoltage());
        sensorEvent.setLinkQuality(event.getMotionSensor().getLinkQuality());

        service.processingSensors(sensorEvent);

    }
}
