package ru.yandex.practicum.processor.hub;

import ru.yandex.practicum.DeviceRemovedEvent;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.service.HubService;

public class DeviceRemovedEventAvroProcessor implements EventProcessor {
    private final HubService service;

    public DeviceRemovedEventAvroProcessor(HubService service) {
        this.service = service;
    }

    @Override
    public void process(HubEventAvro event) {
        DeviceRemovedEvent deviceRemoved = new DeviceRemovedEvent();
        deviceRemoved.setHubId(event.getHubId());
        deviceRemoved.setTimestamp(event.getTimestamp());
        deviceRemoved.setId(((DeviceRemovedEventAvro) event.getPayload()).getId());
        service.processingEvent(deviceRemoved);
    }
}
