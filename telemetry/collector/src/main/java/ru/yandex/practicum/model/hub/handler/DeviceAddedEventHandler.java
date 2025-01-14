package ru.yandex.practicum.model.hub.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.DeviceAddedEvent;
import ru.yandex.practicum.DeviceSensorType;
import ru.yandex.practicum.grpc.telemetry.event.DeviceAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.service.CollectorService;

import java.time.Instant;

import static ru.yandex.practicum.grpc.telemetry.event.HubEventProto.PayloadCase.DEVICE_ADDED;

@Component
@RequiredArgsConstructor
public class DeviceAddedEventHandler implements HubEventHandler {
    private final CollectorService service;

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return DEVICE_ADDED;
    }

    @Override
    public void handle(HubEventProto event) {
        System.out.println("Устройство добавлено");
        DeviceAddedEventProto deviceAdded = event.getDeviceAdded();

        DeviceAddedEvent deviceEvent = new DeviceAddedEvent();
        deviceEvent.setHubId(event.getHubId());
        deviceEvent.setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds()));
        deviceEvent.setId(event.getDeviceAdded().getId());
        deviceEvent.setDeviceType(DeviceSensorType.valueOf(event.getDeviceAdded().getType().name()));

        service.processingHub(deviceEvent);
    }
}
