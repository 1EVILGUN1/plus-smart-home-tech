package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "scenario_actions")
@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ScenarioActionEntity {

    @EmbeddedId
    private ScenarioActionCompositeKey key;

    @ManyToOne
    @JoinColumn(name = "scenario_id")
    private ScenarioEntity scenarioEntity;

    @ManyToOne
    @JoinColumn(name = "sensor_id")
    private SensorEntity sensorEntity;

    @ManyToOne
    @JoinColumn(name = "action_id")
    private ActionEntity actionEntity;
}
