package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.ScenarioActionCompositeKey;
import ru.practicum.model.ScenarioActionEntity;

public interface ScenarioActionRepository extends JpaRepository<ScenarioActionEntity, ScenarioActionCompositeKey> {
}
