package ru.yandex.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.config.HubConsumerConfig;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.processor.hub.*;
import ru.yandex.practicum.service.HubService;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {
    private final HubConsumerConfig hubConsumerConfig;
    private final HubService service;

    private static final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

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
        // Извлекаем настройки из конфигурации
        Properties config = getPropertiesConsumerHub();
        KafkaConsumer<Void, HubEventAvro> consumer = new KafkaConsumer<>(config);

        Map<Class<? extends SpecificRecordBase>, EventProcessor> processors = new HashMap<>();
        processors.put(ScenarioRemovedEventAvro.class, new ScenarioRemovedEventProcessor(service));
        processors.put(ScenarioAddedEventAvro.class, new ScenarioAddedEventProcessor(service));
        processors.put(DeviceRemovedEventAvro.class, new DeviceRemovedEventAvroProcessor(service));
        processors.put(DeviceAddedEventAvro.class, new DeviceAddedEventAvroProcessor(service));

        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
        try {
            consumer.subscribe(hubConsumerConfig.getTopics());

            while (true) {
                ConsumerRecords<Void, HubEventAvro> records = consumer.poll(hubConsumerConfig.getConsumer().getConsumeAttemptTimeout());
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
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, hubConsumerConfig.getBootstrapServers());
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, hubConsumerConfig.getConsumer().getKeyDeserializer());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, hubConsumerConfig.getConsumer().getValueDeserializer());
        config.put(ConsumerConfig.CLIENT_ID_CONFIG, hubConsumerConfig.getClientId());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, hubConsumerConfig.getGroupId());
        return config;
    }
}

