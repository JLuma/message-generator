package ru.sbt.tengri.json.message.generator.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.sbt.tengri.json.message.generator.config.JsonGeneratorConfig;
import ru.sbt.tengri.json.message.generator.config.ConfigurationException;
import ru.sbt.tengri.json.message.generator.json.field.FieldSchema;
import ru.sbt.tengri.json.message.generator.json.field.IntFieldSchema;
import ru.sbt.tengri.json.message.generator.json.field.StringFieldSchema;
import ru.sbt.tengri.json.message.generator.json.field.value.generator.ConstantFieldValue;
import ru.sbt.tengri.json.message.generator.json.field.value.generator.IntValueGenerator;
import ru.sbt.tengri.json.message.generator.json.field.value.generator.StringValueGenerator;
import ru.sbt.tengri.json.message.generator.text.TextGenerator;
import ru.sbt.tengri.json.message.generator.text.word.RandomWordGenerator;
import ru.sbt.tengri.json.message.generator.text.word.VocabularyWordGenerator;
import ru.sbt.tengri.json.message.generator.text.word.WordGenerator;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;

public class JsonSchemaLoader {

    public JsonSchemaLegacy loadJsonSchema(JsonGeneratorConfig jsonGeneratorConfig) {
        JsonNode schemaFileAsJson = loadFileAsJsonNode(jsonGeneratorConfig.getSchemaFileUri());
        return buildJsonSchema(schemaFileAsJson, jsonGeneratorConfig);
    }

    private JsonNode loadFileAsJsonNode(URI schemaFileUri) throws ConfigurationException {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readTree(schemaFileUri.toURL());
        } catch (IOException ex) {
            throw new ConfigurationException(String.format("Is schema file [%s] a valid JSON?", schemaFileUri), ex);
        }
    }

    private JsonSchemaLegacy buildJsonSchema(JsonNode schemaAsJson, JsonGeneratorConfig jsonGeneratorConfig) {
        JsonSchemaLegacy schema = new JsonSchemaLegacy();

        schemaAsJson.fields().forEachRemaining(field -> {
            String schemaFieldName = field.getKey();
            JsonNode fieldNode = field.getValue();

            if (!fieldNode.isValueNode()) {
                throw new ConfigurationException(
                        String.format(
                                "%s field looks like it's not simple value. Provided schema must be flatten json.",
                                schemaFieldName));
            }

            if (fieldNode.isTextual()) {
                schema.addField(constructField(schemaFieldName, fieldNode.asText(), jsonGeneratorConfig));
            } else if (fieldNode.isNumber()) {
                schema.addField(constructConstantIntField(schemaFieldName, fieldNode.asText()));
            }
        });

        return schema;
    }

    private FieldSchema<?> constructField(String fieldName, String fieldValue, JsonGeneratorConfig jsonGeneratorConfig) {
        ValueGeneratorType generatorType = parseValueGeneratorType(fieldValue);
        if (generatorType == null) {
            return constructConstantStringField(fieldName, fieldValue);
        } else {
            return constructGeneratedField(fieldName, generatorType, jsonGeneratorConfig);
        }
    }

    private FieldSchema<?> constructGeneratedField(String fieldName, ValueGeneratorType generatorType, JsonGeneratorConfig jsonGeneratorConfig) {
        switch (generatorType) {
            case GEN_INT:
                return new IntFieldSchema(fieldName, new IntValueGenerator());
            case GEN_STRING:
                return new StringFieldSchema(fieldName, new StringValueGenerator(buildTextGenerator(jsonGeneratorConfig)));
            default:
                throw new IllegalArgumentException();
        }
    }

    private IntFieldSchema constructConstantIntField(String fieldName, String fieldValue) {
        return new IntFieldSchema(fieldName, new ConstantFieldValue<>(Integer.parseInt(fieldValue)));
    }

    private StringFieldSchema constructConstantStringField(String fieldName, String fieldValue) {
        return new StringFieldSchema(fieldName, new ConstantFieldValue<>(fieldValue));
    }

    private TextGenerator buildTextGenerator(JsonGeneratorConfig jsonGeneratorConfig) {
        return new TextGenerator(buildWordGenerator(jsonGeneratorConfig), jsonGeneratorConfig.getTextWordsAmountRange());
    }

    private WordGenerator buildWordGenerator(JsonGeneratorConfig jsonGeneratorConfig) {
        JsonGeneratorConfig.WordGeneratorType wordGeneratorType = jsonGeneratorConfig.getWordGeneratorType();
        switch (wordGeneratorType) {
            case VOCABULARY:
                Path vocabularyFilePath = jsonGeneratorConfig.getVocabularyFilePath()
                        //TODO Get property names from proper source
                        .orElseThrow(() -> new ConfigurationException("Property [text.gen.vocabulary.vocabulary_file]"
                                + " cannot be null, if word generator type is set to"
                                + " vocabulary!"));
                return new VocabularyWordGenerator(vocabularyFilePath);
            case RANDOM:
                return new RandomWordGenerator(jsonGeneratorConfig.getRandomWordLengthRange());
            default:
                throw new IllegalArgumentException("Unknown type of WordGeneratorType: " + wordGeneratorType);
        }
    }

    private ValueGeneratorType parseValueGeneratorType(String valueGeneratorType) {
        try {
            return ValueGeneratorType.valueOf(valueGeneratorType);
        } catch (IllegalArgumentException ignored) {
        }
        return null;
    }

    private enum ValueGeneratorType {
        vocabulary, in, rnd_string, rnd_long
    }

}
