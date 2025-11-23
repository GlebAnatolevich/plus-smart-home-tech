package ru.practicum.kafka.deserializer;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.avro.Schema;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;
import java.util.Arrays;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BaseAvroDeserializer <T extends SpecificRecordBase> implements Deserializer<T> {
    DatumReader<T> datumReader;

    DecoderFactory decoderFactory;

    public BaseAvroDeserializer(Schema schema) {
        this(DecoderFactory.get(), schema);
    }

    public BaseAvroDeserializer(DecoderFactory decoderFactory, Schema schema) {
        this.decoderFactory = decoderFactory;
        this.datumReader = new SpecificDatumReader<>(schema);
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }

        try {
            Decoder decoder = decoderFactory.binaryDecoder(data, null);

            return datumReader.read(null, decoder);
        } catch (IOException ex) {
            throw new SerializationException(String.format("Ошибка десериализации данных для топика [%s]. Data: %s",
                    topic, Arrays.toString(data)), ex);
        }
    }
}
