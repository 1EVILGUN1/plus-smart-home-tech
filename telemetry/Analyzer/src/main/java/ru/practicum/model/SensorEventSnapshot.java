package ru.practicum.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.Map;


@Getter
@Setter
@ToString
public class SensorEventSnapshot {
    private String hubId;
    private Instant timestapm;
    private Map<String, SensorState> sensorsState;
}
