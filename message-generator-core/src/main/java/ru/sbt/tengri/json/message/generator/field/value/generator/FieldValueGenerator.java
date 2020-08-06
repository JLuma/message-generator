package ru.sbt.tengri.json.message.generator.field.value.generator;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = ConstantFieldValue.class, name = ConstantFieldValue.CONSTANT_VALUE_GENERATOR_JSON_TYPE),
    @JsonSubTypes.Type(value = VocabularyGenerator.class, name = VocabularyGenerator.VOCABULARY_GENERATOR_JSON_TYPE),
    @JsonSubTypes.Type(value = IntValueGenerator.class, name = IntValueGenerator.RANDOM_INT_GENERATOR_JSON_TYPE),
    @JsonSubTypes.Type(value = StringValueGenerator.class, name = StringValueGenerator.RANDOM_STRING_GENERATOR_JSON_TYPE),
    @JsonSubTypes.Type(value = RandomTextGenerator.class, name = RandomTextGenerator.RANDOM_TEXT_GENERATOR_JSON_TYPE),
    @JsonSubTypes.Type(value = VocabularyTextGenerator.class, name = VocabularyTextGenerator.VOCABULARY_TEXT_GENERATOR_JSON_TYPE),
    @JsonSubTypes.Type(value = IncrementingTimeFieldGenerator.class, name = IncrementingTimeFieldGenerator.INCREMENTING_TIME_FIELD_GENERATOR_JSON_TYPE)
})
public interface FieldValueGenerator<T> {
    T generateValue();
}
