package org.jluma.tools.loadtesting.syntheticdatagenerator.generator.schema.schema;

import jakarta.annotation.Nullable;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

import org.jluma.tools.loadtesting.syntheticdatagenerator.generator.schema.ConfigurationException;
import org.jluma.tools.loadtesting.syntheticdatagenerator.generator.schema.field.value.generator.FieldValueGenerator;

import java.io.IOException;

public class MessageSchemaLoader {

    private final Path schemaFilePath;

    public MessageSchemaLoader(@Nullable Path schemaFilePath) {
        this.schemaFilePath = schemaFilePath;
    }

    public MessageSchema loadMessageSchema() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            byte[] schemaContent = schemaFilePath != null ? Files.readAllBytes(schemaFilePath) : getDefaultSchema();
            TypeReference<HashMap<String, FieldValueGenerator<?>>> typeRef
                = new TypeReference<HashMap<String, FieldValueGenerator<?>>>() {};
            return new MessageSchema(objectMapper.readValue(schemaContent, typeRef));
        } catch (IOException ex) {
            throw new ConfigurationException(String.format("Is schema file [%s] a valid JSON?", schemaFilePath), ex);
        }
    }

    private byte[] getDefaultSchema() {
        try (InputStream in = MessageSchemaLoader.class.getResourceAsStream("/default-schema.json")) {
            if (in == null) {
                throw new IllegalStateException("Jar is corrupted. Cannot find default-schema.json");
            }
            return in.readAllBytes();
        } catch (IOException ex) {
            throw new UncheckedIOException("Cannot read default-schema.json", ex);
        }
    }

}
