package ru.yandex.practicum;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static ru.yandex.practicum.SensorEventType.CLIMATE_SENSOR_EVENT;


@Getter
@Setter
@ToString
public class ClimateSensorEvent extends SensorEvent {

    private int temperatureC;
    private int humidity;
    private int co2Level;

    @Override
    public ru.yandex.practicum.SensorEventType getType() {
        return CLIMATE_SENSOR_EVENT;
    }
}
