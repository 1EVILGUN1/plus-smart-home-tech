package ru.yandex.practicum.service;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serializer;
import ru.yandex.practicum.kafka.config.KafkaProducerProperties;

import java.util.Properties;

public abstract class ProcessProducerSend {
    public <T, R> void processEvent(T event, Serializer<R> serializer, String topic, String hubId) {
        Properties config = KafkaProducerProperties.kafkaProducerConfig();
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, serializer.getClass());
        Producer<String, T> producer = new KafkaProducer<>(config);
        ProducerRecord<String, T> record = new ProducerRecord<>(topic, hubId, event);
        producer.send(record);
        producer.close();
    }
}
