package ru.yandex.practicum.serialize.hub;

import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.exception.SerializationException;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HexFormat;

public class DeviceAddedAvroSerializer implements Serializer<DeviceAddedEventAvro> {
    private static final Logger log = LoggerFactory.getLogger(DeviceAddedAvroSerializer.class);
    private static final HexFormat hexFormat = HexFormat.ofDelimiter(":");

    @Override
    public byte[] serialize(String topic, DeviceAddedEventAvro event) {
        if (event == null) {
            return null;
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            DatumWriter<DeviceAddedEventAvro> datumWriter = new SpecificDatumWriter<>(DeviceAddedEventAvro.class);
            BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null);

            datumWriter.write(event, encoder);

            encoder.flush();

            byte[] bytes = outputStream.toByteArray();

            log.info("Данные сериализованы в формат Avro:\n{}", hexFormat.formatHex(bytes));

            return bytes;

        } catch (IOException e) {
            throw new SerializationException("Ошибка сериализации", e);
        }

    }

}
