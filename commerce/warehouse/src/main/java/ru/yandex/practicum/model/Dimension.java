package ru.yandex.practicum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class Dimension {
    @Column
    private Double width;
    @Column
    private Double height;
    @Column
    private Double depth;
}