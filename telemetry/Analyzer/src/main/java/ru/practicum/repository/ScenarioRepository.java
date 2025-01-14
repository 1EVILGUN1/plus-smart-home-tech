package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.ScenarioEntity;

import java.util.List;
import java.util.Optional;

public interface ScenarioRepository extends JpaRepository<ScenarioEntity, Long> {
    List<ScenarioEntity> findByHubId(String hubId);

    Optional<ScenarioEntity> findByHubIdAndName(String hubId, String name);
}
