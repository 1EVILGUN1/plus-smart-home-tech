package ru.yandex.practicum.model.hub;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.contant.HubEventType;

import static ru.yandex.practicum.contant.HubEventType.SCENARIO_REMOVED;

@Getter
@Setter
@ToString
public class ScenarioRemovedEvent extends ScenarioEvent {
    @Override
    public HubEventType getType() {
        return SCENARIO_REMOVED;
    }
}
