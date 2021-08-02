package org.jluma.message.generator.schema_based.field.value.generator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jluma.message.generator.schema_based.utils.RandomUtils;

public class BooleanValueGenerator implements FieldValueGenerator<Boolean> {

  public static final String BOOLEAN_VALUE_GENERATOR_JSON_TYPE = "random_boolean";

  @JsonIgnore
  private final RandomUtils randomUtils = new RandomUtils();

  @Override
  public Boolean generateValue() {
    return randomUtils.getRandomBooleanValue();
  }

}
