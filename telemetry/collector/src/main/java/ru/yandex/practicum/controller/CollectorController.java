package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.model.hub.*;
import ru.yandex.practicum.model.sensor.*;
import ru.yandex.practicum.service.CollectorService;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
public class CollectorController {
    private final CollectorService service;

    @PostMapping("/sensors")
    public void processingSensors(@Valid @RequestBody SensorEvent event) {
        switch (event.getClass().getSimpleName()) {
            case "ClimateSensorEvent":
                service.processingSensors((ClimateSensorEvent) event);
                break;
            case "LightSensorEvent":
                service.processingSensors((LightSensorEvent) event);
                break;
            case "MotionSensorEvent":
                service.processingSensors((MotionSensorEvent) event);
                break;
            case "SwitchSensorEvent":
                service.processingSensors((SwitchSensorEvent) event);
                break;
            case "TemperatureSensorEvent":
                service.processingSensors((TemperatureSensorEvent) event);
                break;
        }

    }

    @PostMapping("/hubs")
    public void processingHubs(@Valid @RequestBody HubEvent event) {
        switch (event.getClass().getSimpleName()) {
            case "DeviceAddedEvent":
                service.processingHub((DeviceAddedEvent) event);
                break;
            case "DeviceRemovedEvent":
                service.processingHub((DeviceRemovedEvent) event);
                break;
            case "ScenarioAddedEvent":
                service.processingHub((ScenarioAddedEvent) event);
                break;
            case "ScenarioRemovedEvent":
                service.processingHub((ScenarioRemovedEvent) event);
                break;
        }

    }
}
