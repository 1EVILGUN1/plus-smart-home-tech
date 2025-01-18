package ru.yandex.practicum.processor.hub;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

public interface EventProcessor {
    void process(HubEventAvro event);
}
