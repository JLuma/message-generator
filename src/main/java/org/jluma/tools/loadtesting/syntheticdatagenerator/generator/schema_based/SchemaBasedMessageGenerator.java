package org.jluma.tools.loadtesting.syntheticdatagenerator.generator.schema_based;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.jluma.tools.loadtesting.syntheticdatagenerator.api.MessageGenerator;
import org.jluma.tools.loadtesting.syntheticdatagenerator.api.MessageGeneratorInitializationException;
import org.jluma.tools.loadtesting.syntheticdatagenerator.generator.schema_based.schema.FieldSchema;
import org.jluma.tools.loadtesting.syntheticdatagenerator.generator.schema_based.schema.MessageSchema;
import org.jluma.tools.loadtesting.syntheticdatagenerator.generator.schema_based.schema.MessageSchemaLoader;

public class SchemaBasedMessageGenerator implements MessageGenerator {

  private final Path schemaFilePath;
  private MessageSchema schema;

  public SchemaBasedMessageGenerator(Path schemaPath) {
    this.schemaFilePath = schemaPath;
  }

  @Override
  public void initialize() throws MessageGeneratorInitializationException {
    MessageSchemaLoader schemaLoader = new MessageSchemaLoader(schemaFilePath);
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
