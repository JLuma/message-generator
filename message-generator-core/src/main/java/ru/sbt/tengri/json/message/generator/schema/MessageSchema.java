package ru.sbt.tengri.json.message.generator.schema;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.ToString;
import ru.sbt.tengri.json.message.generator.field.value.generator.FieldValueGenerator;

@ToString
public class MessageSchema {

  private final List<FieldSchema<?>> fieldsSchemas;

  MessageSchema(Map<String, FieldValueGenerator<?>> valueGeneratorByFieldName) {
    this.fieldsSchemas = valueGeneratorByFieldName.entrySet().stream()
                                                         .map(e -> new FieldSchema<>(e.getKey(), e.getValue()))
                                                         .collect(Collectors.toList());
  }

  public List<FieldSchema<?>> getFieldsSchemas() {
    return fieldsSchemas;
  }

}
