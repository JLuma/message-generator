package ru.sbt.tengri.json.message.generator.field.value.generator;

import java.util.UUID;

public class UUIDValueGenerator implements FieldValueGenerator<UUID> {

  public static final String UUID_GENERATOR_JSON_TYPE = "random_uuid";

  @Override
  public UUID generateValue() {
    return UUID.randomUUID();
  }

}
