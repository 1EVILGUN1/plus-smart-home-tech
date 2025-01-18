package ru.yandex.practicum.model.sensor.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.SwitchSensorEvent;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SwitchSensorProto;
import ru.yandex.practicum.service.CollectorService;

import java.time.Instant;

import static ru.yandex.practicum.grpc.telemetry.event.SensorEventProto.PayloadCase.SWITCH_SENSOR;

@Slf4j
@Component
@RequiredArgsConstructor
public class SwitchSensorEventHandler implements SensorEventHandler {
    private final CollectorService service;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SWITCH_SENSOR;
    }

    @Override
    public void handle(SensorEventProto event) {
        try {
            log.info("Получено событие датчика переключателя");
            SwitchSensorProto switchSensor = event.getSwitchSensor();
            log.info("Влажность воздуха: {}", switchSensor.getState());
            SwitchSensorEvent sensorEvent = new SwitchSensorEvent();
            sensorEvent.setId(event.getId());
            sensorEvent.setHubId(event.getHubId());
            sensorEvent.setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds()));
            sensorEvent.setState(event.getSwitchSensor().getState());
            service.processingSensors(sensorEvent);
        } catch (Exception e) {
            log.error("Ошибка обработки {}", e.getMessage());
        }
    }
}
