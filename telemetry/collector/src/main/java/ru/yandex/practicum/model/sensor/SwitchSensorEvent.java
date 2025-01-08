package ru.yandex.practicum.model.sensor;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.contant.SensorEventType;

import static ru.yandex.practicum.contant.SensorEventType.SWITCH_SENSOR_EVENT;

@Getter
@Setter
@ToString
public class SwitchSensorEvent extends SensorEvent {

    private boolean state;

    @Override
    public SensorEventType getType() {
        return SWITCH_SENSOR_EVENT;
    }
}
