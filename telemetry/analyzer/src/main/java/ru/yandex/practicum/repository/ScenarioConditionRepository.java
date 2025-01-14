package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.model.ScenarioConditionCompositeKey;
import ru.yandex.practicum.model.ScenarioConditionEntity;

public interface ScenarioConditionRepository extends JpaRepository<ScenarioConditionEntity, ScenarioConditionCompositeKey> {
}
