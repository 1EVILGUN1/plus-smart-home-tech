package ru.yandex.practicum.model.hub;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.contant.DeviceSensorType;
import ru.yandex.practicum.contant.HubEventType;

import static ru.yandex.practicum.contant.HubEventType.DEVICE_ADDED;

@Getter
@Setter
@ToString
public class DeviceAddedEvent extends HubEvent {

    @NonNull
    private String id;
    @NonNull
    private DeviceSensorType deviceType;

    public HubEventType getType() {
        return DEVICE_ADDED;
    }

}
