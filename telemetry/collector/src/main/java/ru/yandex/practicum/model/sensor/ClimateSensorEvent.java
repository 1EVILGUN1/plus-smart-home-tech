package ru.yandex.practicum.model.sensor;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.contant.SensorEventType;

import static ru.yandex.practicum.contant.SensorEventType.CLIMATE_SENSOR_EVENT;

@Getter
@Setter
@ToString
public class ClimateSensorEvent extends SensorEvent {

    private int temperatureC;
    private int humidity;
    private int co2Level;

    @Override
    public SensorEventType getType() {
        return CLIMATE_SENSOR_EVENT;
    }
}
