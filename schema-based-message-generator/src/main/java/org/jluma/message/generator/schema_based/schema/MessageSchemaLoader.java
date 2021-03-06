package org.jluma.message.generator.schema_based.schema;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;

import org.jluma.message.generator.schema_based.ConfigurationException;
import org.jluma.message.generator.schema_based.field.value.generator.FieldValueGenerator;

import java.io.IOException;
import java.net.URI;

public class MessageSchemaLoader {

    private final URI schemaFileUri;

    public MessageSchemaLoader(URI schemaFileUri) {
        this.schemaFileUri = schemaFileUri;
    }

    public MessageSchema loadMessageSchema() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            TypeReference<HashMap<String, FieldValueGenerator<?>>> typeRef
                = new TypeReference<HashMap<String, FieldValueGenerator<?>>>() {};
            return new MessageSchema(objectMapper.readValue(schemaFileUri.toURL(), typeRef));
        } catch (IOException ex) {
            throw new ConfigurationException(String.format("Is schema file [%s] a valid JSON?", schemaFileUri), ex);
        }
    }

}
