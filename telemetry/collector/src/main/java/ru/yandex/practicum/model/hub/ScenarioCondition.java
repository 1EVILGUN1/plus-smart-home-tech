package ru.yandex.practicum.model.hub;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.contant.OperationType;
import ru.yandex.practicum.contant.OperationCondition;

@Getter
@Setter
@ToString
public class ScenarioCondition {

    private String sensorId;
    private OperationType type;
    private OperationCondition operationCondition;
    private int value;
}
