package ru.yandex.practicum.model.hub;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.contant.DeviceActionType;

@Getter
@Setter
@ToString
public class DeviceAction {
    private String sensorId;
    private DeviceActionType type;
    private int value;
}
