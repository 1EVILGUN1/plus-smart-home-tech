package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "actions")
@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ActionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String type;
    private Integer value;
}
