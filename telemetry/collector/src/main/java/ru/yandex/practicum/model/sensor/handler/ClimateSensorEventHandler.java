package ru.yandex.practicum.model.sensor.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.ClimateSensorEvent;
import ru.yandex.practicum.grpc.telemetry.event.ClimateSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.service.CollectorService;

import java.time.Instant;

import static ru.yandex.practicum.grpc.telemetry.event.SensorEventProto.PayloadCase.CLIMATE_SENSOR;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClimateSensorEventHandler implements SensorEventHandler {
    private final CollectorService service;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return CLIMATE_SENSOR;
    }

    @Override
    public void handle(SensorEventProto event) {
        try {
            log.info("Получено событие климатического датчика");
            ClimateSensorProto climateSensor = event.getClimateSensor();
            log.info("Влажность воздуха: {}", climateSensor.getHumidity());
            ClimateSensorEvent sensorEvent = new ClimateSensorEvent();
            sensorEvent.setId(event.getId());
            sensorEvent.setHubId(event.getHubId());
            sensorEvent.setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds()));
            sensorEvent.setHumidity(event.getClimateSensor().getHumidity());
            sensorEvent.setTemperatureC(event.getClimateSensor().getTemperatureC());
            sensorEvent.setCo2Level(event.getClimateSensor().getCo2Level());
            service.processingSensors(sensorEvent);
        } catch (Exception e) {
            log.error("Ошибка обработки {}", e.getMessage());
        }
    }
}
