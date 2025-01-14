package ru.yandex.practicum;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static ru.yandex.practicum.SensorEventType.SWITCH_SENSOR_EVENT;


@Getter
@Setter
@ToString
public class SwitchSensorEvent extends SensorEvent {

    private boolean state;

    @Override
    public ru.yandex.practicum.SensorEventType getType() {
        return SWITCH_SENSOR_EVENT;
    }
}
