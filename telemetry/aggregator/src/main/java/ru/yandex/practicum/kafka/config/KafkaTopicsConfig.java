package ru.yandex.practicum.kafka.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "kafka.topics")
public class KafkaTopicsConfig {
    private String sensors;   // Топик для событий датчиков
    private String snapshots; // Топик для снимков состояния
}
