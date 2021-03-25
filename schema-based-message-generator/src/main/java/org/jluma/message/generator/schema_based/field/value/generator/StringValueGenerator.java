package org.jluma.message.generator.schema_based.field.value.generator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jluma.message.generator.schema_based.text.word.WordGenerator;
import org.jluma.message.generator.schema_based.field.value.generator.factory.WordGeneratorFactory;

public class StringValueGenerator implements FieldValueGenerator<String> {

    public static final String RANDOM_STRING_GENERATOR_JSON_TYPE = "random_string";

    @JsonIgnore
    private final WordGenerator wordGenerator;

    @JsonCreator
    public StringValueGenerator(
        @JsonProperty("minStringLength") Integer minStringLength,
        @JsonProperty("maxStringLength") Integer maxStringLength) {

        this.wordGenerator = new WordGeneratorFactory().createRandomWordGenerator(minStringLength, maxStringLength);
    }

    @Override
    public String generateValue() {
        return wordGenerator.generateWord();
    }

}
