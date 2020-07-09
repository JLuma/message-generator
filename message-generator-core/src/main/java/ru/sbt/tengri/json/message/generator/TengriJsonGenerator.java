package ru.sbt.tengri.json.message.generator;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import ru.sbt.tengri.json.message.generator.config.JsonGeneratorConfig;
import ru.sbt.tengri.json.message.generator.config.ConfigurationException;
import ru.sbt.tengri.json.message.generator.json.field.FieldSchema;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import ru.sbt.tengri.json.message.generator.schema.JsonSchemaLegacy;
import ru.sbt.tengri.json.message.generator.schema.JsonSchemaLoader;

public class TengriJsonGenerator {

    private final JsonSchemaLegacy schema;

    public TengriJsonGenerator() throws ConfigurationException {
        JsonGeneratorConfig jsonGeneratorConfig = loadConfig();
        JsonSchemaLoader schemaLoader = new JsonSchemaLoader();
        this.schema = schemaLoader.loadJsonSchema(jsonGeneratorConfig);
    }

    private JsonGeneratorConfig loadConfig() throws ConfigurationException {
        String configurationFilePath = System.getProperty("json.gen.configuration.file.path");
        if (configurationFilePath == null) {
            return JsonGeneratorConfig.defaults();
        } else {
            return JsonGeneratorConfig.load(configurationFilePath);
        }
    }

    public String generateMessage() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            JsonGenerator jsonGenerator = new JsonFactory().createGenerator(out);
            jsonGenerator.writeStartObject();

            for (FieldSchema<?> fieldSchema : this.schema.fields()) {
                fieldSchema.createFieldWriter().writeField(jsonGenerator);
            }

            jsonGenerator.writeEndObject();
            jsonGenerator.close();
        } catch (IOException ex) {
            throw new IllegalStateException("Cannot generate JSON message");
        }
        return new String(out.toByteArray());
    }

}
