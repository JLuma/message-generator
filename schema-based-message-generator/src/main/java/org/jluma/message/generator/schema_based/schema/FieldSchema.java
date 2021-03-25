package org.jluma.message.generator.schema_based.schema;

import lombok.Value;
import org.jluma.message.generator.schema_based.field.value.generator.FieldValueGenerator;

@Value
public class FieldSchema<T> {
  String name;
  FieldValueGenerator<T> fieldValueGenerator;
}
