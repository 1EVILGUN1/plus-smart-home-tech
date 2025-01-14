package ru.yandex.practicum;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
public class ScenarioCondition {

    private String sensorId;
    private ConditionType type;
    private ConditionOperation conditionOperation;
    private int value;
}
