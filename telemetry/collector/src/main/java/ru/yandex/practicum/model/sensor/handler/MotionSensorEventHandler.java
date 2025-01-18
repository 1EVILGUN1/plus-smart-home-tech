package ru.yandex.practicum.model.sensor.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.MotionSensorEvent;
import ru.yandex.practicum.grpc.telemetry.event.MotionSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.service.CollectorService;

import java.time.Instant;

import static ru.yandex.practicum.grpc.telemetry.event.SensorEventProto.PayloadCase.MOTION_SENSOR;

@Slf4j
@Component
@RequiredArgsConstructor
public class MotionSensorEventHandler implements SensorEventHandler {
    private final CollectorService service;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return MOTION_SENSOR;
    }

    @Override
    public void handle(SensorEventProto event) {
        try {
            log.info("Получено событие датчика движения");
            MotionSensorProto motionSensor = event.getMotionSensor();
            log.info("Наличие движения: {}", motionSensor.getMotion());
            MotionSensorEvent sensorEvent = new MotionSensorEvent();
            sensorEvent.setId(event.getId());
            sensorEvent.setHubId(event.getHubId());
            sensorEvent.setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds()));
            sensorEvent.setMotion(event.getMotionSensor().getMotion());
            sensorEvent.setVoltage(event.getMotionSensor().getVoltage());
            sensorEvent.setLinkQuality(event.getMotionSensor().getLinkQuality());
            service.processingSensors(sensorEvent);
        } catch (Exception e) {
            log.error("Ошибка обработки {}", e.getMessage());
        }
    }
}
