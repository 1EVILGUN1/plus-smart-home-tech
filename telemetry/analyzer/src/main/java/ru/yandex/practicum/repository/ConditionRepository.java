package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.model.ConditionEntity;

public interface ConditionRepository extends JpaRepository<ConditionEntity, Long> {
}
