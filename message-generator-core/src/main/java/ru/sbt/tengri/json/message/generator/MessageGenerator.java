package ru.sbt.tengri.json.message.generator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import ru.sbt.tengri.json.message.generator.schema.FieldSchema;
import ru.sbt.tengri.json.message.generator.schema.MessageSchema;
import ru.sbt.tengri.json.message.generator.schema.MessageSchemaLoader;

public class MessageGenerator {

  private final MessageSchema schema;

  public MessageGenerator(URI schemaFileUri) {
    MessageSchemaLoader schemaLoader = new MessageSchemaLoader(schemaFileUri);
    this.schema = schemaLoader.loadMessageSchema();
  }

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
