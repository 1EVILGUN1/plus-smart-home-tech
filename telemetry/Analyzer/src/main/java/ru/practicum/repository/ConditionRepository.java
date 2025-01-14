package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.ConditionEntity;

public interface ConditionRepository extends JpaRepository<ConditionEntity, Long> {
}
