package ru.yandex.practicum;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import static ru.yandex.practicum.HubEventType.DEVICE_ADDED;


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
