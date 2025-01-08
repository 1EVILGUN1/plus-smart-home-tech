package ru.yandex.practicum.service;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.VoidSerializer;
import ru.yandex.practicum.serialize.GeneralAvroSerializer;

import java.util.Properties;

public abstract class Process {
    public <T, R> void processEvent(T event, Serializer<R> serializer, String topic, String hubId) {
        Properties config = getProperties();
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, serializer.getClass());
        Producer<String, T> producer = new KafkaProducer<>(config);
        ProducerRecord<String, T> record = new ProducerRecord<>(topic, hubId, event);
        producer.send(record);
        producer.close();
    }

    public Properties getProperties() {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, VoidSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GeneralAvroSerializer.class);
        return config;
    }
}
