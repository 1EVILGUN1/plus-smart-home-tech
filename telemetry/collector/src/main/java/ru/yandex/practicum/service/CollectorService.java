package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.VoidSerializer;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.hub.*;
import ru.yandex.practicum.model.sensor.*;
import ru.yandex.practicum.serialize.hub.DeviceAddedAvroSerializer;
import ru.yandex.practicum.serialize.hub.DeviceRemovedAvroSerializer;
import ru.yandex.practicum.serialize.sensor.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
@Slf4j
@RequiredArgsConstructor
public class CollectorService {

    public void processingSensors(ClimateSensorEvent sensorEvent) {
        ClimateSensorAvro event = new ClimateSensorAvro(
                sensorEvent.getTemperatureC(), sensorEvent.getHumidity(), sensorEvent.getCo2Level());


        Properties config = getPropertiesSensor();
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ClimateSensorAvroSerializer.class);

        Producer<String, ClimateSensorAvro> producer = new KafkaProducer<>(config);
        String topic = "telemetry.sensors.v1";

        ProducerRecord<String, ClimateSensorAvro> record = new ProducerRecord<>(topic, event);

        producer.send(record);
        producer.close();
    }

    public void processingSensors(LightSensorEvent sensorEvent) {
        LightSensorAvro event = new LightSensorAvro(sensorEvent.getLinkQuality(), sensorEvent.getLuminosity());

        Properties config = getPropertiesSensor();
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, LightSensorAvroSerializer.class);

        Producer<String, LightSensorAvro> producer = new KafkaProducer<>(config);
        String topic = "telemetry.sensors.v1";

        ProducerRecord<String, LightSensorAvro> record = new ProducerRecord<>(topic, event);

        producer.send(record);
        producer.close();
    }

    public void processingSensors(MotionSensorEvent sensorEvent) {
        MotionSensorAvro event = new MotionSensorAvro
                (sensorEvent.getLinkQuality(), sensorEvent.isMotion(), sensorEvent.getVoltage());

        Properties config = getPropertiesSensor();
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, MotionSensorAvroSerializer.class);

        Producer<String, MotionSensorAvro> producer = new KafkaProducer<>(config);
        String topic = "telemetry.sensors.v1";

        ProducerRecord<String, MotionSensorAvro> record = new ProducerRecord<>(topic, event);

        producer.send(record);
        producer.close();

    }

    public void processingSensors(SwitchSensorEvent sensorEvent) {
        SwitchSensorAvro event = new SwitchSensorAvro
                (sensorEvent.isState());

        Properties config = getPropertiesSensor();
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, SwitchSensorAvroSerializer.class);

        Producer<String, SwitchSensorAvro> producer = new KafkaProducer<>(config);
        String topic = "telemetry.sensors.v1";

        ProducerRecord<String, SwitchSensorAvro> record = new ProducerRecord<>(topic, event);

        producer.send(record);
        producer.close();

    }

    public void processingSensors(TemperatureSensorEvent sensorEvent) {
        TemperatureSensorAvro event = new TemperatureSensorAvro
                (sensorEvent.getTemperatureC(), sensorEvent.getTemperatureF());

        Properties config = getPropertiesSensor();
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, TemperatureSensorAvroSerializer.class);

        Producer<String, TemperatureSensorAvro> producer = new KafkaProducer<>(config);
        String topic = "telemetry.sensors.v1";

        ProducerRecord<String, TemperatureSensorAvro> record = new ProducerRecord<>(topic, event);

        producer.send(record);
        producer.close();

    }

    public void processingHub(DeviceAddedEvent hubEvent) {
        DeviceTypeAvro deviceTypeAvro = DeviceTypeAvro.valueOf(hubEvent.getDeviceType().name());
        DeviceAddedEventAvro event = new DeviceAddedEventAvro(hubEvent.getId(), deviceTypeAvro);

        Properties config = getPropertiesHub();
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, DeviceAddedAvroSerializer.class);

        Producer<String, DeviceAddedEventAvro> producer = new KafkaProducer<>(config);
        String topic = "telemetry.hubs.v1";

        ProducerRecord<String, DeviceAddedEventAvro> record = new ProducerRecord<>(topic, event);

        producer.send(record);
        producer.close();

    }

    public void processingHub(DeviceRemovedEvent hubEvent) {
        DeviceRemovedEventAvro event = new DeviceRemovedEventAvro(hubEvent.getId());

        Properties config = getPropertiesHub();
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, DeviceRemovedAvroSerializer.class);

        Producer<String, DeviceRemovedEventAvro> producer = new KafkaProducer<>(config);
        String topic = "telemetry.hubs.v1";

        ProducerRecord<String, DeviceRemovedEventAvro> record = new ProducerRecord<>(topic, event);

        producer.send(record);
        producer.close();

    }

    public void processingHub(ScenarioAddedEvent hubEvent) {
        List<ScenarioConditionAvro> conditionAvros = new ArrayList<>();
        for (ScenarioCondition sc : hubEvent.getConditions()) {
            conditionAvros.add(new ScenarioConditionAvro(sc.getSensorId()
                    , DeviceTypeAvro.valueOf(sc.getType().name())
                    , ConditionOperationAvro.valueOf(sc.getOperation().name())
                    , sc.getValue()));
        }
        List<DeviceActionAvro> deviceActionAvros = new ArrayList<>();
        for (DeviceAction da : hubEvent.getActions()) {
            deviceActionAvros.add(new DeviceActionAvro(da.getSensorId(), ActionTypeAvro.valueOf(da.getType().name()),
                    da.getValue()));
        }

        ScenarioAddedEventAvro event = new ScenarioAddedEventAvro(hubEvent.getName(), conditionAvros, deviceActionAvros);

        Properties config = getPropertiesHub();
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, DeviceRemovedAvroSerializer.class);

        Producer<String, ScenarioAddedEventAvro> producer = new KafkaProducer<>(config);
        String topic = "telemetry.hubs.v1";

        ProducerRecord<String, ScenarioAddedEventAvro> record = new ProducerRecord<>(topic, event);

        producer.send(record);
        producer.close();

    }

    public void processingHub(ScenarioRemovedEvent hubEvent) {
        ScenarioRemovedEvent event = new ScenarioRemovedEvent();

        Properties config = getPropertiesHub();
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, DeviceRemovedAvroSerializer.class);

        Producer<String, ScenarioRemovedEvent> producer = new KafkaProducer<>(config);
        String topic = "telemetry.hubs.v1";

        ProducerRecord<String, ScenarioRemovedEvent> record = new ProducerRecord<>(topic, event);

        producer.send(record);
        producer.close();

    }

    private Properties getPropertiesSensor() {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, VoidSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, TemperatureSensorAvroSerializer.class);
        return config;
    }

    private Properties getPropertiesHub() {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, VoidSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, TemperatureSensorAvroSerializer.class);
        return config;
    }

}
