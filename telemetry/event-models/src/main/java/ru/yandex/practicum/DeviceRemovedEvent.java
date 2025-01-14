package ru.yandex.practicum;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import static ru.yandex.practicum.HubEventType.DEVICE_REMOVED;


@Getter
@Setter
@ToString
public class DeviceRemovedEvent extends HubEvent {

    @NonNull
    private String id;

    public HubEventType getType() {
        return DEVICE_REMOVED;
    }
}
