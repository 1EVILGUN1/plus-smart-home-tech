package ru.yandex.practicum.model.hub.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.ScenarioAddedEvent;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.mapper.ScenarioAddedEventMapper;
import ru.yandex.practicum.service.CollectorService;

import static ru.yandex.practicum.grpc.telemetry.event.HubEventProto.PayloadCase.SCENARIO_ADDED;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScenarioAddedEventHandler implements HubEventHandler {
    private final CollectorService service;

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return SCENARIO_ADDED;
    }

    @Override
    public void handle(HubEventProto event) {
        try {
            log.info("Сценарий добавлен {}", event.getHubId());
            ScenarioAddedEvent scenario = ScenarioAddedEventMapper.mapHubEventProtoToScenarioAddedEvent(event);
            service.processingHub(scenario);
        } catch (Exception e) {
            log.error("Ошибка обработки {}", e.getMessage());
        }
    }
}
