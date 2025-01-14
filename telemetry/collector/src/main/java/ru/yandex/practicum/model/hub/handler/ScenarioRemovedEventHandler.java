package ru.yandex.practicum.model.hub.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.ScenarioRemovedEvent;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.service.CollectorService;

import java.time.Instant;

import static ru.yandex.practicum.grpc.telemetry.event.HubEventProto.PayloadCase.SCENARIO_REMOVED;

@Component
@RequiredArgsConstructor
public class ScenarioRemovedEventHandler implements HubEventHandler {
    private final CollectorService service;

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return SCENARIO_REMOVED;
    }

    @Override
    public void handle(HubEventProto event) {
        System.out.println("Сценарий удалён");

        ScenarioRemovedEvent scenario = new ScenarioRemovedEvent();
        scenario.setHubId(event.getHubId());
        scenario.setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds()));
        scenario.setName(event.getScenarioRemoved().getName());

        service.processingHub(scenario);
    }
}
