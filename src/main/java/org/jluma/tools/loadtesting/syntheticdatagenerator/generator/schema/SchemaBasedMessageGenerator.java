package org.jluma.tools.loadtesting.syntheticdatagenerator.generator.schema;

import org.jluma.tools.loadtesting.syntheticdatagenerator.config.GeneratorProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.jluma.tools.loadtesting.syntheticdatagenerator.api.MessageGenerator;
import org.jluma.tools.loadtesting.syntheticdatagenerator.generator.schema.schema.FieldSchema;
import org.jluma.tools.loadtesting.syntheticdatagenerator.generator.schema.schema.MessageSchema;
import org.jluma.tools.loadtesting.syntheticdatagenerator.generator.schema.schema.MessageSchemaLoader;

@Component
@ConditionalOnProperty(name = "generator.mode", havingValue = "SCHEMA_BASED")
public class SchemaBasedMessageGenerator implements MessageGenerator {

  private final MessageSchema schema;

  public SchemaBasedMessageGenerator(GeneratorProperties properties) {
    final Path schemaPath = properties.getSchemaBased().getSchemaFilePath();
    MessageSchemaLoader schemaLoader = new MessageSchemaLoader(schemaPath);
    this.schema = schemaLoader.loadMessageSchema();
  }

  @Override
  public String generateMessage() {
    Map<String, Object> targetJsonAsMap = new HashMap<>();
    for (FieldSchema<?> fieldSchema : schema.getFieldsSchemas()) {
      targetJsonAsMap.put(fieldSchema.getName(), fieldSchema.getFieldValueGenerator().generateValue());
    }
    try {
      return new ObjectMapper().writeValueAsString(targetJsonAsMap);
    } catch (JacksonException ex) {
      throw new RuntimeException("Cannot marshal object to json", ex);
    }
  }

}
