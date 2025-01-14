package ru.practicum.model;


import jakarta.persistence.*;
import lombok.*;

@Table(name = "scenario_conditions")
@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ScenarioConditionEntity {
    @EmbeddedId
    private ScenarioConditionCompositeKey key;

    @ManyToOne
    @JoinColumn(name = "scenario_id")
    private ScenarioEntity scenarioEntity;

    @ManyToOne
    @JoinColumn(name = "sensor_id")
    private SensorEntity sensorEntity;

    @ManyToOne
    @JoinColumn(name = "condition_id")
    private ConditionEntity conditionEntity;
}
