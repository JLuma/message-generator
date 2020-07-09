package ru.sbt.tengri.json.message.generator.json.field;

import ru.sbt.tengri.json.message.generator.json.field.value.generator.FieldValueGenerator;
import ru.sbt.tengri.json.message.generator.json.field.writer.FieldWriter;

public abstract class FieldSchema<T> {

    private final String name;
    private final FieldValueGenerator<T> fieldValueGenerator;

    public FieldSchema(String name, FieldValueGenerator<T> fieldValueGenerator) {
        this.name = name;
        this.fieldValueGenerator = fieldValueGenerator;
    }

    public String getName() {
        return name;
    }

    public FieldValueGenerator<T> getFieldValueGenerator() {
        return fieldValueGenerator;
    }

    public abstract FieldWriter createFieldWriter();

}
