package ru.yandex.practicum.model.sensor.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.TemperatureSensorEvent;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.TemperatureSensorProto;
import ru.yandex.practicum.service.CollectorService;

import java.time.Instant;

import static ru.yandex.practicum.grpc.telemetry.event.SensorEventProto.PayloadCase.TEMPERATURE_SENSOR;

@Component
@RequiredArgsConstructor
public class TemperatureSensorEventHandler implements SensorEventHandler {
    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return TEMPERATURE_SENSOR;
    }

    private final CollectorService service;

    @Override
    public void handle(SensorEventProto event) {
        System.out.println("Получено событие климатического датчика");
        // получаем данные климатического датчика
        TemperatureSensorProto temperatureSensor = event.getTemperatureSensor();
        System.out.println("ВТемпература в градусах Цельсия: " + temperatureSensor.getTemperatureC());
        System.out.println("ВТемпература в градусах Фаренгейта: " + temperatureSensor.getTemperatureF());

        TemperatureSensorEvent sensorEvent = new TemperatureSensorEvent();
        sensorEvent.setId(event.getId());
        sensorEvent.setHubId(event.getHubId());
        sensorEvent.setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds()));
        sensorEvent.setTemperatureC(event.getTemperatureSensor().getTemperatureC());
        sensorEvent.setTemperatureF(event.getTemperatureSensor().getTemperatureF());

        service.processingSensors(sensorEvent);
    }
}
