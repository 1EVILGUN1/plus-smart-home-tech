package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.model.ActionEntity;

public interface ActionRepository extends JpaRepository<ActionEntity, Long> {
}
