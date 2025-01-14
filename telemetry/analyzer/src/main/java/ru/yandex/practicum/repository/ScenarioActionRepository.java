package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.model.ScenarioActionCompositeKey;
import ru.yandex.practicum.model.ScenarioActionEntity;

public interface ScenarioActionRepository extends JpaRepository<ScenarioActionEntity, ScenarioActionCompositeKey> {
}
