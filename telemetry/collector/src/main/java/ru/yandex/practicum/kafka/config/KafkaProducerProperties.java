package ru.yandex.practicum.kafka.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KafkaProducerProperties {

    @Value("${kafka.producer.bootstrap-servers}")
    private static String bootstrapServers;

    @Value("${kafka.producer.key-serializer}")
    private static String keySerializer;

    @Value("${kafka.producer.value-serializer}")
    private static String valueSerializer;

    @Bean
    public static Properties kafkaProducerConfig() {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);
        return config;
    }
}
