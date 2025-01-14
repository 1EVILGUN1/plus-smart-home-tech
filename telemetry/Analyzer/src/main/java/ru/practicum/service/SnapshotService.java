package ru.practicum.service;

import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.grpc.ScenarioRealizer;
import ru.practicum.model.*;
import ru.practicum.repository.ScenarioConditionRepository;
import ru.practicum.repository.ScenarioRepository;
import ru.practicum.repository.SensorRepository;
import ru.yandex.practicum.*;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SnapshotService {

    private final ScenarioRepository scenarioRepository;
    private final ScenarioConditionRepository scenarioConditionRepository;
    private final SensorRepository sensorRepository;
    private final ScenarioRealizer scenarioRealizer;

    public void processingSnapshot(SensorEventSnapshot snapshot) {
        System.out.println("Получен снапшот");
        String hubId = snapshot.getHubId();

        List<ScenarioEntity> scenarioEntityList = scenarioRepository.findByHubId(hubId);

        if (scenarioEntityList.size() == 0) {
            System.out.println("Нет доступных сценариев");
            return;
        }
        Set<String> keys = snapshot.getSensorsState().keySet();

        for (String key : keys) {
            SensorEvent sensorEvent = snapshot.getSensorsState().get(key).getData();
            Optional<SensorEntity> sensorEntity = sensorRepository.findByIdAndHubId(sensorEvent.getId(), sensorEvent.getHubId());
            if (sensorEntity.isEmpty()) {
                System.out.println("Датчик не найден");
                continue;
            }
            List<ScenarioEntity> actualScenarios = scenarioEntityList.stream()
                    .filter(x -> x.getScenarioConditions().containsKey(sensorEntity))
                    .toList();

            DeviceActionRequest request = null;
            switch (sensorEvent.getType()) {
                case CLIMATE_SENSOR_EVENT:
                    ClimateSensorEvent climateSensorEvent = (ClimateSensorEvent) sensorEvent;
                    for (ScenarioEntity se : actualScenarios) {
                        ConditionEntity conditionEntity = se.getScenarioConditions().get(sensorEntity);
                        if (ConditionType.CO2LEVEL.equals(conditionEntity.getType())) {
                            if (compare(climateSensorEvent.getCo2Level(), conditionEntity.getValue(),
                                    ConditionOperation.valueOf(conditionEntity.getOperation()))) {

                                request = createDeviceActionRequest(se, climateSensorEvent.getId(), hubId);

                            }
                        }
                        if (ConditionType.HUMIDITY.equals(conditionEntity.getType())) {
                            if (compare(climateSensorEvent.getHumidity(), conditionEntity.getValue(),
                                    ConditionOperation.valueOf(conditionEntity.getOperation()))) {

                                request = createDeviceActionRequest(se, climateSensorEvent.getId(), hubId);
                            }
                        }
                    }
                    break;
                case LIGHT_SENSOR_EVENT:
                    LightSensorEvent lightSensorEvent = (LightSensorEvent) sensorEvent;
                    for (ScenarioEntity se : actualScenarios) {
                        ConditionEntity lightConditionEntity = se.getScenarioConditions().get(sensorEntity);
                        if (compare(lightSensorEvent.getLuminosity(), lightConditionEntity.getValue(),
                                ConditionOperation.valueOf(lightConditionEntity.getOperation()))) {

                            request = createDeviceActionRequest(se, lightSensorEvent.getId(), hubId);
                        }
                    }
                    break;
                case MOTION_SENSOR_EVENT:
                    MotionSensorEvent motionSensorEvent = (MotionSensorEvent) sensorEvent;
                    for (ScenarioEntity se : actualScenarios) {
                        ConditionEntity motionConditionEntity = se.getScenarioConditions().get(sensorEntity);
                        if ((motionSensorEvent.isMotion() ? 1 : 0) == motionConditionEntity.getValue()) {

                            request = createDeviceActionRequest(se, motionSensorEvent.getId(), hubId);
                        }
                    }
                    break;
                case SWITCH_SENSOR_EVENT:
                    SwitchSensorEvent switchSensorEvent = (SwitchSensorEvent) sensorEvent;
                    for (ScenarioEntity se : actualScenarios) {
                        ConditionEntity switchConditionEntity = se.getScenarioConditions().get(sensorEntity);
                        if ((switchSensorEvent.isState() ? 1 : 0) == switchConditionEntity.getValue()) {

                            request = createDeviceActionRequest(se, switchSensorEvent.getId(), hubId);
                        }
                    }
                    break;
                case TEMPERATURE_SENSOR_EVENT:
                    TemperatureSensorEvent temperatureSensorEvent = (TemperatureSensorEvent) sensorEvent;
                    for (ScenarioEntity se : actualScenarios) {
                        ConditionEntity temperatureConditionEntity = se.getScenarioConditions().get(sensorEntity);
                        if (compare(temperatureSensorEvent.getTemperatureC(), temperatureConditionEntity.getValue(),
                                ConditionOperation.valueOf((temperatureConditionEntity.getOperation())))) {

                            request = createDeviceActionRequest(se, temperatureSensorEvent.getId(), hubId);
                        }
                    }
                    break;
            }

            scenarioRealizer.send(request);
        }

    }


    private boolean compare(Integer first, Integer second, ConditionOperation opration) {
        switch (opration) {
            case EQUALS:
                return first == second;
            case GREATER_THAN:
                return first > second;
            case LOWER_THAN:
                return first < second;
        }
        return false;
    }

    private DeviceActionRequest createDeviceActionRequest(ScenarioEntity se, String id, String hubId) {
        ActionEntity actionEtity = se.getScenarioActions().get(id);
        DeviceActionProto deviceActionProto = DeviceActionProto.newBuilder()
                .setSensorId(id)
                .setType(ActionTypeProto.valueOf(actionEtity.getType()))
                .setValue(actionEtity.getValue())
                .build();
        DeviceActionRequest request = DeviceActionRequest.newBuilder()
                .setHubId(hubId)
                .setScenarioName(se.getName())
                .setActionEntity(deviceActionProto)
                .setTimestamp(Timestamp.newBuilder().setSeconds(Instant.now().getEpochSecond()).build())
                .build();

        return request;
    }

}
