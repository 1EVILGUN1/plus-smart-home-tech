package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.ConditionOperation;
import ru.yandex.practicum.ConditionType;

@Table(name = "conditions")
@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ConditionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private ConditionType type;
    @Enumerated(EnumType.STRING)
    private ConditionOperation operation;
    private Integer value;
}
