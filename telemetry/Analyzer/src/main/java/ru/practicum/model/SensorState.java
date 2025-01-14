package ru.practicum.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.SensorEvent;

import java.time.Instant;


@Getter
@Setter
@ToString
public class SensorState {
    private Instant timestamp = Instant.now();

    private SensorEvent data;

}
