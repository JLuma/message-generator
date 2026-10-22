package org.jluma.tools.loadtesting.syntheticdatagenerator.generator.schema.schema;

import lombok.Value;
import org.jluma.tools.loadtesting.syntheticdatagenerator.generator.schema.field.value.generator.FieldValueGenerator;

@Value
public class FieldSchema<T> {
  String name;
  FieldValueGenerator<T> fieldValueGenerator;
}
