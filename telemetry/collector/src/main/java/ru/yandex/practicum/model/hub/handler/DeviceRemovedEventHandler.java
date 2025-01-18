package ru.yandex.practicum.model.hub.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.DeviceRemovedEvent;
import ru.yandex.practicum.grpc.telemetry.event.DeviceRemovedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.service.CollectorService;

import java.time.Instant;

import static ru.yandex.practicum.grpc.telemetry.event.HubEventProto.PayloadCase.DEVICE_REMOVED;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceRemovedEventHandler implements HubEventHandler {
    private final CollectorService service;

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return DEVICE_REMOVED;
    }

    @Override
    public void handle(HubEventProto event) {
        try {
            log.info("Устройство добавлено {}", event.getHubId());
            DeviceRemovedEventProto deviceRemoved = event.getDeviceRemoved();
            DeviceRemovedEvent deviceEvent = new DeviceRemovedEvent();
            deviceEvent.setHubId(event.getHubId());
            deviceEvent.setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds()));
            deviceEvent.setId(event.getDeviceRemoved().getId());
            service.processingHub(deviceEvent);
        } catch (Exception e) {
            log.error("Ошибка обработки {}", e.getMessage());
        }
    }
}
