package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.ScenarioAddedException;
import ru.practicum.model.*;
import ru.practicum.repository.*;
import ru.yandex.practicum.*;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class HubService {

    private final SensorRepository sensorRepository;
    private final ScenarioRepository scenarioRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;
    private final ScenarioConditionRepository scenarioConditionRepository;
    private final ScenarioActionRepository scenarioActionRepository;

    public void processingEvent(DeviceAddedEvent event) {
        if (!sensorRepository.existsById(event.getId())) {
            SensorEntity sensorEntity = new SensorEntity(event.getId(), event.getHubId());
            sensorRepository.save(sensorEntity);
            System.out.println("Добавлено устройство");
        }
    }

    public void processingEvent(DeviceRemovedEvent event) {
        if (sensorRepository.existsById(event.getId())) {
            SensorEntity sensorEntity = new SensorEntity(event.getId(), event.getHubId());
            sensorRepository.delete(sensorEntity);
            System.out.println("Устройство удалено");
        }
    }

    @Transactional
    public void processingEvent(ScenarioAddedEvent event) {
        ScenarioEntity scenarioEntity = new ScenarioEntity();
        Optional<ScenarioEntity> scenarioOptional = scenarioRepository.findByHubIdAndName(event.getHubId(), event.getName());
        if (scenarioOptional.isPresent()) {
            System.out.println("Сценарий уже существует");
            throw new ScenarioAddedException("Сценарий уже существует");
        }
        scenarioEntity.setHubId(event.getHubId());
        scenarioEntity.setName(event.getName());
        scenarioEntity = scenarioRepository.save(scenarioEntity);

        for (ScenarioCondition sc : event.getConditions()) {
            ConditionEntity conditionEntity = new ConditionEntity();
            conditionEntity.setType(sc.getType().name());
            conditionEntity.setOperation(sc.getConditionOperation().name());
            conditionEntity.setValue(sc.getValue());
            conditionRepository.save(conditionEntity);
            Optional<SensorEntity> sensorEntity = sensorRepository.findByIdAndHubId(sc.getSensorId(), event.getHubId());
            if (sensorEntity.isEmpty()) {
                System.out.println("Нужный сенсор не найден");
                throw new ScenarioAddedException("Нужный сенсор не найден");
            }
            ScenarioConditionEntity scenarioConditionEntity = new ScenarioConditionEntity();
            scenarioConditionEntity.setScenarioEntity(scenarioEntity);
            scenarioConditionEntity.setSensorEntity(sensorEntity.get());
            scenarioConditionEntity.setConditionEntity(conditionEntity);
            scenarioConditionRepository.save(scenarioConditionEntity);
        }
        for (DeviceActionEvent da : event.getActions()) {
            ActionEntity actionEntity = new ActionEntity();
            actionEntity.setType(da.getType().name());
            actionEntity.setValue(da.getValue());
            actionRepository.save(actionEntity);
            Optional<SensorEntity> sensorEntity = sensorRepository.findByIdAndHubId(da.getSensorId(), event.getHubId());
            if (sensorEntity.isEmpty()) {
                System.out.println("Нужный сенсор не найден");
                throw new ScenarioAddedException("Нужный сенсор не найден");
            }
            ScenarioActionEntity scenarioActionEntity = new ScenarioActionEntity();
            scenarioActionEntity.setScenarioEntity(scenarioEntity);
            scenarioActionEntity.setSensorEntity(sensorEntity.get());
            scenarioActionEntity.setActionEntity(actionEntity);
            scenarioActionRepository.save(scenarioActionEntity);
        }
        System.out.println("Сценарий добавлен");
    }

    public void processingEvent(ScenarioRemovedEvent event) {
        Optional<ScenarioEntity> scenarioOptional = scenarioRepository.findByHubIdAndName(event.getHubId(), event.getName());
        if (scenarioOptional.isPresent()) {
            scenarioRepository.delete(scenarioOptional.get());
            System.out.println("Сценарий удалён");
        }
    }
}
