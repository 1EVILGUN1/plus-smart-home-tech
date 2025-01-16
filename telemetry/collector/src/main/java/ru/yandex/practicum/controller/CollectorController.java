package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.*;
import ru.yandex.practicum.service.CollectorService;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/events")
public class CollectorController {

    private final CollectorService service;

    @PostMapping("/sensors")
    public void processingSensors(@Valid @RequestBody SensorEvent event) {
        switch (event.getType()) {
            case SensorEventType.CLIMATE_SENSOR_EVENT:
                service.processingSensors((ClimateSensorEvent) event);
                break;
            case SensorEventType.LIGHT_SENSOR_EVENT:
                service.processingSensors((LightSensorEvent) event);
                break;
            case SensorEventType.MOTION_SENSOR_EVENT:
                service.processingSensors((MotionSensorEvent) event);
                break;
            case SensorEventType.SWITCH_SENSOR_EVENT:
                service.processingSensors((SwitchSensorEvent) event);
                break;
            case SensorEventType.TEMPERATURE_SENSOR_EVENT:
                service.processingSensors((TemperatureSensorEvent) event);
                break;
        }

    }

    @PostMapping("/hubs")
    public void processingHubs(@Valid @RequestBody HubEvent event) {
        switch (event.getType()) {
            case HubEventType.DEVICE_ADDED:
                service.processingHub((DeviceAddedEvent) event);
                break;
            case HubEventType.DEVICE_REMOVED:
                service.processingHub((DeviceRemovedEvent) event);
                break;
            case HubEventType.SCENARIO_ADDED:
                service.processingHub((ScenarioAddedEvent) event);
                break;
            case HubEventType.SCENARIO_REMOVED:
                service.processingHub((ScenarioRemovedEvent) event);
                break;
        }

    }
}
