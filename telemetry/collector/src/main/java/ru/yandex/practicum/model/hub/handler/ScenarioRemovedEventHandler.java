package ru.yandex.practicum.model.hub.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.ScenarioRemovedEvent;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.service.CollectorService;

import java.time.Instant;

import static ru.yandex.practicum.grpc.telemetry.event.HubEventProto.PayloadCase.SCENARIO_REMOVED;

@Slf4j
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
        try {
            log.info("Сценарий удалён {}", event.getHubId());
            ScenarioRemovedEvent scenario = new ScenarioRemovedEvent();
            scenario.setHubId(event.getHubId());
            scenario.setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds()));
            scenario.setName(event.getScenarioRemoved().getName());
            service.processingHub(scenario);
        } catch (Exception e) {
            log.error("Ошибка обработки {}", e.getMessage());
        }
    }
}
