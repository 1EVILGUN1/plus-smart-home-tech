package ru.yandex.practicum.model.sensor.handler;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SwitchSensorProto;

import static ru.yandex.practicum.grpc.telemetry.event.SensorEventProto.PayloadCase.SWITCH_SENSOR;

@Component
public class SwitchSensorEventHandler implements SensorEventHandler{
    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SWITCH_SENSOR;
    }

    @Override
    public void handle(SensorEventProto event) {
        System.out.println("Получено событие датчика переключателя");
        SwitchSensorProto switchSensor = event.getSwitchSensor();
        System.out.println("Влажность воздуха: " + switchSensor.getState());
    }
}
