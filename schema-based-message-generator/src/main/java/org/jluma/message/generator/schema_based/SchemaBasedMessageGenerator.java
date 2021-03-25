package org.jluma.message.generator.schema_based;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.jluma.message.generator.api.MessageGenerator;
import org.jluma.message.generator.api.MessageGeneratorInitializationException;
import org.jluma.message.generator.schema_based.schema.FieldSchema;
import org.jluma.message.generator.schema_based.schema.MessageSchema;
import org.jluma.message.generator.schema_based.schema.MessageSchemaLoader;

public class SchemaBasedMessageGenerator implements MessageGenerator {

  private final URI schemaFileUri;
  private MessageSchema schema;

  public SchemaBasedMessageGenerator(URI schemaFileUri) {
    this.schemaFileUri = schemaFileUri;
  }

  @Override
  public void initialize() throws MessageGeneratorInitializationException {
    MessageSchemaLoader schemaLoader = new MessageSchemaLoader(schemaFileUri);
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
    } catch (JsonProcessingException ex) {
      throw new RuntimeException("Cannot marshal object to json", ex);
    }
  }

}
