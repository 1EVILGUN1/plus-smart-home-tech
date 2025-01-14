package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.SensorEntity;

import java.util.Collection;
import java.util.Optional;

public interface SensorRepository extends JpaRepository<SensorEntity, String> {
    boolean existsByIdInAndHubId(Collection<String> ids, String hubId);

    Optional<SensorEntity> findByIdAndHubId(String id, String hubId);
}
