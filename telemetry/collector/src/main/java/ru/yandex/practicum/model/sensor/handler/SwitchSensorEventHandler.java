package ru.yandex.practicum.model.sensor.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.SwitchSensorEvent;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SwitchSensorProto;
import ru.yandex.practicum.service.CollectorService;

import java.time.Instant;

import static ru.yandex.practicum.grpc.telemetry.event.SensorEventProto.PayloadCase.SWITCH_SENSOR;

@Component
@RequiredArgsConstructor
public class SwitchSensorEventHandler implements SensorEventHandler {
    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SWITCH_SENSOR;
    }

    private final CollectorService service;

    @Override
    public void handle(SensorEventProto event) {
        System.out.println("Получено событие датчика переключателя");
        // получаем данные датчика переключателя
        SwitchSensorProto switchSensor = event.getSwitchSensor();
        System.out.println("Влажность воздуха: " + switchSensor.getState());

        SwitchSensorEvent sensorEvent = new SwitchSensorEvent();
        sensorEvent.setId(event.getId());
        sensorEvent.setHubId(event.getHubId());
        sensorEvent.setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds()));
        sensorEvent.setState(event.getSwitchSensor().getState());

        service.processingSensors(sensorEvent);
    }
}
