package ru.yandex.practicum.model.hub;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public abstract class ScenarioEvent extends HubEvent {
    @NotBlank
    @Size(min = 3)
    private String name;
}
