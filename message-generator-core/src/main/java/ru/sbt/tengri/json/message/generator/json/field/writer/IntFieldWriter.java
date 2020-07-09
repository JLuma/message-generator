package ru.sbt.tengri.json.message.generator.json.field.writer;

import com.fasterxml.jackson.core.JsonGenerator;
import ru.sbt.tengri.json.message.generator.json.field.FieldSchema;

import java.io.IOException;

public class IntFieldWriter implements FieldWriter {

    private final FieldSchema<Integer> fieldSchema;

    public IntFieldWriter(FieldSchema<Integer> fieldSchema) {
        this.fieldSchema = fieldSchema;
    }

    @Override
    public void writeField(JsonGenerator generator) throws IOException {
        generator.writeNumberField(fieldSchema.getName(), fieldSchema.getFieldValueGenerator().generateValue());
    }

}