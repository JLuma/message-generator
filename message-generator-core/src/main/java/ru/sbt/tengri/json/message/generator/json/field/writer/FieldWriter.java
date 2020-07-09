package ru.sbt.tengri.json.message.generator.json.field.writer;

import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;

public interface FieldWriter {
    void writeField(JsonGenerator generator) throws IOException;
}
