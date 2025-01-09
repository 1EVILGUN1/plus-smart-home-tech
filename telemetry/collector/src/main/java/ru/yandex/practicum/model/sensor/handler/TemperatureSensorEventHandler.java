package ru.yandex.practicum.model.sensor.handler;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.TemperatureSensorProto;

import static ru.yandex.practicum.grpc.telemetry.event.SensorEventProto.PayloadCase.TEMPERATURE_SENSOR;

@Component
public class TemperatureSensorEventHandler implements SensorEventHandler {
    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return TEMPERATURE_SENSOR;
    }

    @Override
    public void handle(SensorEventProto event) {
        System.out.println("Получено событие климатического датчика");
        TemperatureSensorProto temperatureSensor = event.getTemperatureSensor();
        System.out.println("ВТемпература в градусах Цельсия: " + temperatureSensor.getTemperatureC());
        System.out.println("ВТемпература в градусах Фаренгейта: " + temperatureSensor.getTemperatureF());
    }
}
