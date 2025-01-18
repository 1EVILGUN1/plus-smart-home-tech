package ru.yandex.practicum;

import lombok.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class DeviceActionEvent {
    private String sensorId;
    private ActionType type;
    private int value;
}
