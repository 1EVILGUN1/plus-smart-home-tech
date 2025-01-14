package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.ScenarioConditionCompositeKey;
import ru.practicum.model.ScenarioConditionEntity;

public interface ScenarioConditionRepository extends JpaRepository<ScenarioConditionEntity, ScenarioConditionCompositeKey> {
}
