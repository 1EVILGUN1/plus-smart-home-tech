package ru.yandex.practicum.model.sensor.handler;

import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

public interface SensorEventHandler {
    SensorEventProto.PayloadCase getMessageType();

    void handle(SensorEventProto event);
}
