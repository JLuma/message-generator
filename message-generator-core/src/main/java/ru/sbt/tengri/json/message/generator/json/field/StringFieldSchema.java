package ru.sbt.tengri.json.message.generator.json.field;

import ru.sbt.tengri.json.message.generator.json.field.value.generator.FieldValueGenerator;
import ru.sbt.tengri.json.message.generator.json.field.writer.FieldWriter;
import ru.sbt.tengri.json.message.generator.json.field.writer.StringFieldWriter;

public class StringFieldSchema extends FieldSchema<String> {

    public StringFieldSchema(String name, FieldValueGenerator<String> fieldValueGenerator) {
        super(name, fieldValueGenerator);
    }

    @Override
    public FieldWriter createFieldWriter() {
        return new StringFieldWriter(this);
    }

}
