package ru.yandex.practicum.model.sensor;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.contant.SensorEventType;

@Getter
@Setter
@ToString
public class LightSensorEvent extends SensorEvent {

    private int linkQuality;
    private int luminosity;

    @Override
    public SensorEventType getType() {
        return SensorEventType.LIGHT_SENSOR_EVENT;
    }
}
