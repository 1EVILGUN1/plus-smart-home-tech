package ru.yandex.practicum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class ScenarioActionCompositeKey implements Serializable {
    @Column(name = "scenario_id", insertable = false, updatable = false)
    private Long scenarioId;
    @Column(name = "sensor_id", insertable = false, updatable = false)
    private String sensorId;
    @Column(name = "action_id", insertable = false, updatable = false)
    private Long actionId;
}
