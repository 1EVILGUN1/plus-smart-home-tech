package ru.yandex.practicum.model.sensor.handler;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.MotionSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import static ru.yandex.practicum.grpc.telemetry.event.SensorEventProto.PayloadCase.MOTION_SENSOR;

@Component
public class MotionSensorEventHandler implements SensorEventHandler{
    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return MOTION_SENSOR;
    }

    @Override
    public void handle(SensorEventProto event) {
        System.out.println("Получено событие датчика движения");
        MotionSensorProto motionSensor = event.getMotionSensor();
        System.out.println("Наличие движения: " + motionSensor.getMotion());
    }
}
