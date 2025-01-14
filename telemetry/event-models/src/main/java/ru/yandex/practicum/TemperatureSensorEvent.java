package ru.yandex.practicum;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static ru.yandex.practicum.SensorEventType.TEMPERATURE_SENSOR_EVENT;


@Getter
@Setter
@ToString
public class TemperatureSensorEvent extends SensorEvent {

    private int temperatureC;
    private int temperatureF;

    @Override
    public ru.yandex.practicum.SensorEventType getType() {
        return TEMPERATURE_SENSOR_EVENT;
    }
}
