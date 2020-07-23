package ru.sbt.tengri.json.message.generator.schema;

import lombok.Value;
import ru.sbt.tengri.json.message.generator.field.value.generator.FieldValueGenerator;

@Value
public class FieldSchema<T> {
  String name;
  FieldValueGenerator<T> fieldValueGenerator;
}
