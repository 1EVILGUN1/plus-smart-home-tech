package ru.yandex.practicum.processor.hub;

import ru.yandex.practicum.ScenarioAddedEvent;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.mapper.ScenarioAddedMapper;
import ru.yandex.practicum.service.HubService;

public class ScenarioAddedEventProcessor implements EventProcessor {
    private final HubService service;

    public ScenarioAddedEventProcessor(HubService service) {
        this.service = service;
    }

    @Override
    public void process(HubEventAvro event) {
        ScenarioAddedEvent scenarioAddedEvent = ScenarioAddedMapper.mapScenarioAddedAvroToScenarioAddedEvent(event);
        service.processingEvent(scenarioAddedEvent);
    }
}
