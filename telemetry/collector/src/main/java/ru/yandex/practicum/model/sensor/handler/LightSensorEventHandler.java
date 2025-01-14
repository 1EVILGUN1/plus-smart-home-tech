package ru.yandex.practicum.model.sensor.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.LightSensorEvent;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.service.CollectorService;

import java.time.Instant;

import static ru.yandex.practicum.grpc.telemetry.event.SensorEventProto.PayloadCase.LIGHT_SENSOR;

@Component
@RequiredArgsConstructor
public class LightSensorEventHandler implements SensorEventHandler {
    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return LIGHT_SENSOR;
    }

    private final CollectorService service;

    @Override
    public void handle(SensorEventProto event) {
        System.out.println("Получено событие датчика освещённости");
        // получаем данные датчика освещённости
        LightSensorProto lightSensor = event.getLightSensor();
        System.out.println("Уровень освещённости: " + lightSensor.getLuminosity());

        LightSensorEvent sensorEvent = new LightSensorEvent();
        sensorEvent.setId(event.getId());
        sensorEvent.setHubId(event.getHubId());
        sensorEvent.setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds()));
        sensorEvent.setLuminosity(event.getLightSensor().getLuminosity());
        sensorEvent.setLinkQuality(event.getLightSensor().getLinkQuality());

        service.processingSensors(sensorEvent);
    }
}
