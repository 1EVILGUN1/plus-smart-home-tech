package ru.yandex.practicum.model.sensor.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.TemperatureSensorEvent;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.TemperatureSensorProto;
import ru.yandex.practicum.service.CollectorService;

import java.time.Instant;

import static ru.yandex.practicum.grpc.telemetry.event.SensorEventProto.PayloadCase.TEMPERATURE_SENSOR;

@Slf4j
@Component
@RequiredArgsConstructor
public class TemperatureSensorEventHandler implements SensorEventHandler {
    private final CollectorService service;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return TEMPERATURE_SENSOR;
    }

    @Override
    public void handle(SensorEventProto event) {
        try {
            log.info("Получено событие климатического датчика");
            TemperatureSensorProto temperatureSensor = event.getTemperatureSensor();
            log.info("ВТемпература в градусах Цельсия: {}", temperatureSensor.getTemperatureC());
            log.info("ВТемпература в градусах Фаренгейта: {}", temperatureSensor.getTemperatureF());
            TemperatureSensorEvent sensorEvent = new TemperatureSensorEvent();
            sensorEvent.setId(event.getId());
            sensorEvent.setHubId(event.getHubId());
            sensorEvent.setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds()));
            sensorEvent.setTemperatureC(event.getTemperatureSensor().getTemperatureC());
            sensorEvent.setTemperatureF(event.getTemperatureSensor().getTemperatureF());
            service.processingSensors(sensorEvent);
        } catch (Exception e) {
            log.error("Ошибка обработки {}", e.getMessage());
        }
    }
}
