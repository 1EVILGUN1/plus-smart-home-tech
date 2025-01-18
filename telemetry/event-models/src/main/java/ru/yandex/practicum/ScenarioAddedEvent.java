package ru.yandex.practicum;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

import static ru.yandex.practicum.HubEventType.SCENARIO_ADDED;


@Getter
@Setter
@ToString
public class ScenarioAddedEvent extends ScenarioEvent {


    @NonNull
    @Size(min = 1)
    List<DeviceActionEvent> actions;
    @NonNull
    @Size(min = 1)
    List<ScenarioCondition> conditions;

    public HubEventType getType() {
        return SCENARIO_ADDED;
    }
}
