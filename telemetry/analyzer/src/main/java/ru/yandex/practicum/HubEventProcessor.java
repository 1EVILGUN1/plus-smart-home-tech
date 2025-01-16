package ru.yandex.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.VoidDeserializer;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.processor.hub.*;
import ru.yandex.practicum.serialize.HubEventDeserializer;
import ru.yandex.practicum.service.HubService;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {
    private static final List<String> topics = List.of("telemetry.hubs.v1");
    private static final Duration consume_attempt_timeout = Duration.ofMillis(1000);
    private static final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
    private final HubService service;

    private static void manageOffsets(ConsumerRecord<Void, HubEventAvro> record, int count, KafkaConsumer<Void, HubEventAvro> consumer) {
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1)
        );

        if (OffsetCommitCondition.shouldCommit(count)) {
            consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if (exception != null) {
                    log.warn("Ошибка во время фиксации оффсетов: {}", offsets, exception);
                }
            });
        }
    }

    @Override
    public void run() {
        Properties config = getPropertiesConsumerHub();
        KafkaConsumer<Void, HubEventAvro> consumer = new KafkaConsumer<>(config);
        Map<Class<? extends SpecificRecordBase>, EventProcessor> processors = new HashMap<>();
        processors.put(ScenarioRemovedEventAvro.class, new ScenarioRemovedEventProcessor(service));
        processors.put(ScenarioAddedEventAvro.class, new ScenarioAddedEventProcessor(service));
        processors.put(DeviceRemovedEventAvro.class, new DeviceRemovedEventAvroProcessor(service));
        processors.put(DeviceAddedEventAvro.class, new DeviceAddedEventAvroProcessor(service));

        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
        try {
            consumer.subscribe(topics);

            while (true) {
                ConsumerRecords<Void, HubEventAvro> records = consumer.poll(consume_attempt_timeout);
                int count = 0;
                for (ConsumerRecord<Void, HubEventAvro> record : records) {
                    log.info("Получено сообщение. topic: telemetry.hubs.v1 {}\n", record.value());

                    SpecificRecordBase payload = record.value();
                    Class<? extends SpecificRecordBase> payloadClass = payload.getClass();

                    EventProcessor processor = processors.get(payloadClass);
                    if (processor != null) {
                        try {
                            processor.process(record.value());
                        } catch (Exception e) {
                            log.error("Ошибка при обработке события: {}", e.getMessage(), e);
                        }
                    } else {
                        log.warn("Обработчик для типа события {} не найден", payloadClass.getName());
                    }

                    manageOffsets(record, count, consumer);
                    count++;
                }
                consumer.commitAsync();
            }
        } catch (WakeupException ignored) {
            // Игнорируем исключение при остановке
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {
            try {
                consumer.commitSync(currentOffsets);
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
            }
        }
    }


    private Properties getPropertiesConsumerHub() {
        Properties config = new Properties();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, VoidDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, HubEventDeserializer.class);
        config.put(ConsumerConfig.CLIENT_ID_CONFIG, "SomeConsumer2");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "some.group.id2");
        return config;
    }
}
