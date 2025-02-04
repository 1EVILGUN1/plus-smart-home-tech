package ru.yandex.practicum.kafka.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "kafka")
@Configuration
@Data
public class SensorKafkaConfig {

    private ConsumerConfig consumer;
    private ProducerConfig producer;

    @Data
    public static class ConsumerConfig {
        private String bootstrapServers;
        private String groupId;
        private String clientId;
        private String keyDeserializer;
        private String valueDeserializer;
    }

    @Data
    public static class ProducerConfig {
        private String bootstrapServers;
        private String keySerializer;
        private String valueSerializer;
    }
}

