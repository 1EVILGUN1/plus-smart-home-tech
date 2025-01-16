package ru.yandex.practicum.processor.hub;

import ru.yandex.practicum.ScenarioRemovedEvent;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.service.HubService;

public class ScenarioRemovedEventProcessor implements EventProcessor {
    private final HubService service;

    public ScenarioRemovedEventProcessor(HubService service) {
        this.service = service;
    }

    @Override
    public void process(HubEventAvro event) {
        ScenarioRemovedEvent removedEvent = new ScenarioRemovedEvent();
        removedEvent.setHubId(event.getHubId());
        removedEvent.setTimestamp(event.getTimestamp());
        removedEvent.setName(((ScenarioRemovedEventAvro) event.getPayload()).getName());
        service.processingEvent(removedEvent);
    }
}
