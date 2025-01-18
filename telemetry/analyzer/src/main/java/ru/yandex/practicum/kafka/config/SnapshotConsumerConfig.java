package ru.yandex.practicum.kafka.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@ConfigurationProperties(prefix = "kafka.snapshot-consumer")
@Component
@Data
public class SnapshotConsumerConfig {
    private String bootstrapServers;
    private String groupId;
    private String clientId;
    private List<String> topics;
    private Consumer consumer;

    @Data
    public static class Consumer {
        private String keyDeserializer;
        private String valueDeserializer;
        private Duration consumeAttemptTimeout;

    }
}
