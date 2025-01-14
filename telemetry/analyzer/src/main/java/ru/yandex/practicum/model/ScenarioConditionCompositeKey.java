package ru.yandex.practicum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class ScenarioConditionCompositeKey implements Serializable {
    @Column(name = "scenario_id", insertable = false, updatable = false)
    private Long scenarioId;
    @Column(name = "sensor_id", insertable = false, updatable = false)
    private String sensorId;
    @Column(name = "condition_id", insertable = false, updatable = false)
    private Long conditionId;
}
