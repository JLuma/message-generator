package ru.sbt.tengri.json.message.generator.json.field.writer;

import com.fasterxml.jackson.core.JsonGenerator;
import ru.sbt.tengri.json.message.generator.json.field.FieldSchema;

import java.io.IOException;

public class StringFieldWriter implements FieldWriter {

    private final FieldSchema<String> fieldSchema;

    public StringFieldWriter(FieldSchema<String> fieldSchema) {
        this.fieldSchema = fieldSchema;
    }

    @Override
    public void writeField(JsonGenerator generator) throws IOException {
        generator.writeStringField(fieldSchema.getName(), fieldSchema.getFieldValueGenerator().generateValue());
    }

}