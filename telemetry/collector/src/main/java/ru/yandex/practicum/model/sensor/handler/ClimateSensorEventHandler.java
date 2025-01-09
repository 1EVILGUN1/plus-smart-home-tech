package ru.yandex.practicum.model.sensor.handler;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ClimateSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import static ru.yandex.practicum.grpc.telemetry.event.SensorEventProto.PayloadCase.CLIMATE_SENSOR;

@Component
public class ClimateSensorEventHandler implements SensorEventHandler{
    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return CLIMATE_SENSOR;
    }

    @Override
    public void handle(SensorEventProto event) {
        System.out.println("Получено событие климатического датчика");
        ClimateSensorProto climateSensor = event.getClimateSensor();
        System.out.println("Влажность воздуха: " + climateSensor.getHumidity());
    }
}
