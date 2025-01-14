package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Map;

@Table(name = "scenarios")
@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ScenarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NonNull
    private String hubId;
    private String name;

    @JoinTable(name = "scenario_conditions", joinColumns = {@JoinColumn(name = "scenario_id")},
            inverseJoinColumns = @JoinColumn(name = "condition_id")
    )
    @MapKeyColumn(name = "sensor_id")
    @OneToMany(cascade = CascadeType.ALL)
    private Map<SensorEntity, ConditionEntity> scenarioConditions;

    @JoinTable(name = "scenario_actions", joinColumns = {@JoinColumn(name = "scenario_id")},
            inverseJoinColumns = @JoinColumn(name = "action_id")
    )
    @MapKeyColumn(name = "sensor_id")
    @OneToMany(cascade = CascadeType.ALL)
    private Map<SensorEntity, ActionEntity> scenarioActions;
}
