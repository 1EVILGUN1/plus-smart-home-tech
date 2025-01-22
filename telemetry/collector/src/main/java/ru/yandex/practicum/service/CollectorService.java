package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.*;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.serialize.GeneralAvroSerializer;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CollectorService extends ProcessProducerSend {

    @Value("${topic.telemetry.sensors}")
    private String topicTelemetrySensors;
    @Value("${topic.telemetry.hubs}")
    private String topicTelemetryHubs;

    public void processingSensors(ClimateSensorEvent sensorEvent) {
        ClimateSensorAvro event = new ClimateSensorAvro(
                sensorEvent.getTemperatureC(), sensorEvent.getHumidity(), sensorEvent.getCo2Level());
        log.trace("Сохраняю событие {} связанное с хабом {} в топик {}", event, sensorEvent.getHubId(), topicTelemetrySensors);
        processEvent(event, new GeneralAvroSerializer(), topicTelemetrySensors, sensorEvent.getHubId());
    }

    public void processingSensors(LightSensorEvent sensorEvent) {
        LightSensorAvro event = new LightSensorAvro(sensorEvent.getLinkQuality(), sensorEvent.getLuminosity());
        log.trace("Сохраняю событие {} связанное с хабом {} в топик {}", event, sensorEvent.getHubId(), topicTelemetrySensors);
        processEvent(event, new GeneralAvroSerializer(), topicTelemetrySensors, sensorEvent.getHubId());
    }

    public void processingSensors(MotionSensorEvent sensorEvent) {
        MotionSensorAvro event = new MotionSensorAvro
                (sensorEvent.getLinkQuality(), sensorEvent.isMotion(), sensorEvent.getVoltage());
        log.trace("Сохраняю событие {} связанное с хабом {} в топик {}", event, sensorEvent.getHubId(), topicTelemetrySensors);
        processEvent(event, new GeneralAvroSerializer(), topicTelemetrySensors, sensorEvent.getHubId());
    }

    public void processingSensors(SwitchSensorEvent sensorEvent) {
        SwitchSensorAvro event = new SwitchSensorAvro
                (sensorEvent.isState());
        log.trace("Сохраняю событие {} связанное с хабом {} в топик {}", event, sensorEvent.getHubId(), topicTelemetrySensors);
        processEvent(event, new GeneralAvroSerializer(), topicTelemetrySensors, sensorEvent.getHubId());
    }

    public void processingSensors(TemperatureSensorEvent sensorEvent) {
        TemperatureSensorAvro event = new TemperatureSensorAvro
                (sensorEvent.getTemperatureC(), sensorEvent.getTemperatureF());
        log.trace("Сохраняю событие {} связанное с хабом {} в топик {}", event, sensorEvent.getHubId(), topicTelemetrySensors);
        processEvent(event, new GeneralAvroSerializer(), topicTelemetrySensors, sensorEvent.getHubId());
    }

    public void processingHub(DeviceAddedEvent hubEvent) {
        DeviceTypeAvro deviceTypeAvro = DeviceTypeAvro.valueOf(hubEvent.getDeviceType().name());
        DeviceAddedEventAvro event = new DeviceAddedEventAvro(hubEvent.getId(), deviceTypeAvro);
        log.trace("Сохраняю событие {} связанное с хабом {} в топик {}", event, hubEvent.getHubId(), topicTelemetryHubs);
        processEvent(event, new GeneralAvroSerializer(), topicTelemetryHubs, hubEvent.getHubId());
    }

    public void processingHub(DeviceRemovedEvent hubEvent) {
        DeviceRemovedEventAvro event = new DeviceRemovedEventAvro(hubEvent.getId());
        log.trace("Сохраняю событие {} связанное с хабом {} в топик {}", event, hubEvent.getHubId(), topicTelemetryHubs);
        processEvent(event, new GeneralAvroSerializer(), topicTelemetryHubs, hubEvent.getHubId());
    }

    public void processingHub(ScenarioAddedEvent hubEvent) {
        List<ScenarioConditionAvro> conditionAvros = new ArrayList<>();
        for (ScenarioCondition sc : hubEvent.getConditions()) {
            conditionAvros.add(new ScenarioConditionAvro(sc.getSensorId(),
                    ConditionTypeAvro.valueOf(sc.getType().name()),
                    ConditionOperationAvro.valueOf(sc.getConditionOperation().name()),
                    sc.getValue()));
        }
        List<DeviceActionAvro> deviceActionAvros = new ArrayList<>();
        for (DeviceActionEvent da : hubEvent.getActions()) {
            deviceActionAvros.add(new DeviceActionAvro(da.getSensorId(), ActionTypeAvro.valueOf(da.getType().name()),
                    da.getValue()));
        }

        ScenarioAddedEventAvro event = new ScenarioAddedEventAvro(hubEvent.getName(), conditionAvros, deviceActionAvros);
        log.trace("Сохраняю событие {} связанное с хабом {} в топик {}", event, hubEvent.getHubId(), topicTelemetryHubs);
        processEvent(event, new GeneralAvroSerializer(), topicTelemetryHubs, hubEvent.getHubId());
    }

    public void processingHub(ScenarioRemovedEvent hubEvent) {
        ScenarioRemovedEvent event = new ScenarioRemovedEvent();
        log.trace("Сохраняю событие {} связанное с хабом {} в топик {}", event, hubEvent.getHubId(), topicTelemetryHubs);
        processEvent(event, new GeneralAvroSerializer(), topicTelemetryHubs, hubEvent.getHubId());
    }

}
