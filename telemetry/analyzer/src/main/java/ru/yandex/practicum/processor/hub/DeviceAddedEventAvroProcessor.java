package ru.yandex.practicum.processor.hub;

import ru.yandex.practicum.DeviceAddedEvent;
import ru.yandex.practicum.DeviceSensorType;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.service.HubService;

public class DeviceAddedEventAvroProcessor implements EventProcessor {
    private final HubService service;

    public DeviceAddedEventAvroProcessor(HubService service) {
        this.service = service;
    }

    @Override
    public void process(HubEventAvro event) {
        DeviceAddedEvent deviceAdded = new DeviceAddedEvent();
        deviceAdded.setHubId(event.getHubId());
        deviceAdded.setTimestamp(event.getTimestamp());
        deviceAdded.setId(((DeviceAddedEventAvro) event.getPayload()).getId());
        deviceAdded.setDeviceType(DeviceSensorType.valueOf(((DeviceAddedEventAvro) event.getPayload()).getType().name()));
        service.processingEvent(deviceAdded);
    }
}
