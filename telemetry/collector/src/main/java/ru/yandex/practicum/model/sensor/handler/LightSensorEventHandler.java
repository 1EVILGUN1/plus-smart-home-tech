package ru.yandex.practicum.model.sensor.handler;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import static ru.yandex.practicum.grpc.telemetry.event.SensorEventProto.PayloadCase.LIGHT_SENSOR;

@Component
public class LightSensorEventHandler implements SensorEventHandler{
    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return LIGHT_SENSOR;
    }

    @Override
    public void handle(SensorEventProto event) {
        System.out.println("Получено событие датчика освещённости");
        LightSensorProto lightSensor = event.getLightSensor();
        System.out.println("Уровень освещённости: " + lightSensor.getLuminosity());
    }
}
