package ru.yandex.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.VoidSerializer;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.serialize.SensorEventDeserializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {
    private static final List<String> topics = List.of("telemetry.sensors.v1");
    private static final Duration consume_attempt_timeout = Duration.ofMillis(1000);
    private static final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    private static void manageOffsets(ConsumerRecord<Void, String> record, int count, KafkaConsumer<Void, String> consumer) {
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1)
        );

        if (count % 10 == 0) {
            consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if (exception != null) {
                    log.warn("Ошибка во время фиксации оффсетов: {}", offsets, exception);
                }
            });
        }
    }

    public void start() {
        Properties config = getPropertiesSensor();
        KafkaConsumer<Void, String> consumer = new KafkaConsumer<>(config);
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

        try {
            consumer.subscribe(topics);
            while (true) {
                ConsumerRecords<Void, String> records = consumer.poll(consume_attempt_timeout);

                int count = 0;
                for (ConsumerRecord<Void, String> record : records) {
                    manageOffsets(record, count, consumer);
                    count++;
                }
                consumer.commitAsync();
            }


        } catch (
                WakeupException ignored) {
        } catch (
                Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {

            try {

            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
            }
        }
    }

    private Properties getPropertiesSensor() {
        Properties config = new Properties();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, VoidSerializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorEventDeserializer.class);
        config.put(ConsumerConfig.CLIENT_ID_CONFIG, "SomeConsumer");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "some.group.id");
        return config;
    }
}
