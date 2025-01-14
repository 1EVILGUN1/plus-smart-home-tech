package ru.yandex.practicum.model.sensor.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.ClimateSensorEvent;
import ru.yandex.practicum.grpc.telemetry.event.ClimateSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.service.CollectorService;

import java.time.Instant;

import static ru.yandex.practicum.grpc.telemetry.event.SensorEventProto.PayloadCase.CLIMATE_SENSOR;

@Component
@RequiredArgsConstructor
public class ClimateSensorEventHandler implements SensorEventHandler {
    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return CLIMATE_SENSOR;
    }

    private final CollectorService service;

    @Override
    public void handle(SensorEventProto event) {
        System.out.println("Получено событие климатического датчика");
        // получаем данные климатического датчика
        ClimateSensorProto climateSensor = event.getClimateSensor();
        System.out.println("Влажность воздуха: " + climateSensor.getHumidity());

        ClimateSensorEvent sensorEvent = new ClimateSensorEvent();
        sensorEvent.setId(event.getId());
        sensorEvent.setHubId(event.getHubId());
        sensorEvent.setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds()));
        sensorEvent.setHumidity(event.getClimateSensor().getHumidity());
        sensorEvent.setTemperatureC(event.getClimateSensor().getTemperatureC());
        sensorEvent.setCo2Level(event.getClimateSensor().getCo2Level());

        service.processingSensors(sensorEvent);
    }
}
