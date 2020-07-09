package ru.sbt.tengri.json.message.generator.schema;

import ru.sbt.tengri.json.message.generator.json.field.FieldSchema;

import java.util.ArrayList;
import java.util.List;

public class JsonSchemaLegacy {

    private final List<FieldSchema<?>> fieldSchemas = new ArrayList<>();

    public List<FieldSchema<?>> fields() {
        return new ArrayList<>(fieldSchemas);
    }

    public void addField(FieldSchema<?> fieldSchema) {
        fieldSchemas.add(fieldSchema);
    }

}
