package ru.yandex.practicum;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static ru.yandex.practicum.HubEventType.SCENARIO_REMOVED;


@Getter
@Setter
@ToString
public class ScenarioRemovedEvent extends ScenarioEvent {
    @Override
    public HubEventType getType() {
        return SCENARIO_REMOVED;
    }
}
