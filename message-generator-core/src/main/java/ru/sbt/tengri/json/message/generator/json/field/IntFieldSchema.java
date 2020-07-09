package ru.sbt.tengri.json.message.generator.json.field;

import ru.sbt.tengri.json.message.generator.json.field.value.generator.FieldValueGenerator;
import ru.sbt.tengri.json.message.generator.json.field.writer.FieldWriter;
import ru.sbt.tengri.json.message.generator.json.field.writer.IntFieldWriter;

public class IntFieldSchema extends FieldSchema<Integer> {

    public IntFieldSchema(String name, FieldValueGenerator<Integer> fieldValueGenerator) {
        super(name, fieldValueGenerator);
    }

    @Override
    public FieldWriter createFieldWriter() {
        return new IntFieldWriter(this);
    }

}
