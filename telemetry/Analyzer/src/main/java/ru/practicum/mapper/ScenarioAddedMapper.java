package ru.practicum.mapper;

import ru.yandex.practicum.*;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;

public class ScenarioAddedMapper {
    public static ScenarioAddedEvent mapScenarioAddedAvroToScenarioAddedEvent(HubEventAvro eventAvro) {
        ScenarioAddedEvent scenarioAddedEvent = new ScenarioAddedEvent();
        scenarioAddedEvent.setHubId(eventAvro.getHubId());
        scenarioAddedEvent.setTimestamp(eventAvro.getTimestamp());
        ScenarioAddedEventAvro scenarioAvro = (ScenarioAddedEventAvro) eventAvro.getPayload();
        scenarioAddedEvent.setName(scenarioAvro.getName());
        scenarioAddedEvent.setConditions(scenarioAvro.getConditions().stream().map(x -> mapConditionAvroToCondition(x)).toList());
        scenarioAddedEvent.setActions(scenarioAvro.getActions().stream()
                .map(x -> new DeviceActionEvent(x.getSensorId(), ActionType.valueOf(x.getType().name()), x.getValue())).toList());
        return scenarioAddedEvent;
    }

    private static ScenarioCondition mapConditionAvroToCondition(ScenarioConditionAvro conditionAvro) {
        ScenarioCondition condition = new ScenarioCondition();
        condition.setSensorId(conditionAvro.getSensorId());
        condition.setType(ConditionType.valueOf(conditionAvro.getType().name()));
        condition.setConditionOperation(ConditionOperation.valueOf(conditionAvro.getOperation().name()));
        condition.setValue((int) conditionAvro.getValue());

        return condition;
    }
}
