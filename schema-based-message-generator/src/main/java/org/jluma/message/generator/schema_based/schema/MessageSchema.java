package org.jluma.message.generator.schema_based.schema;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.ToString;
import org.jluma.message.generator.schema_based.field.value.generator.FieldValueGenerator;

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
