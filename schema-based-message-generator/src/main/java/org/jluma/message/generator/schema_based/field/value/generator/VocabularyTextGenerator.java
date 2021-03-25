package org.jluma.message.generator.schema_based.field.value.generator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

import org.jluma.message.generator.schema_based.text.TextGenerator;
import org.jluma.message.generator.schema_based.text.word.VocabularyWordGenerator;
import org.jluma.message.generator.schema_based.field.value.generator.factory.TextGeneratorFactory;
import org.jluma.message.generator.schema_based.utils.validation.ValidationUtils;

public class VocabularyTextGenerator implements FieldValueGenerator<String> {

  public static final String VOCABULARY_TEXT_GENERATOR_JSON_TYPE = "vocabulary_text";

  @JsonIgnore
  private final TextGenerator textGenerator;

  public VocabularyTextGenerator(
      @JsonProperty("vocabulary") List<String> vocabulary,
      @JsonProperty("wordsDelimiter") String wordsDelimiter,
      @JsonProperty("minTextWordsAmount") Integer minTextWordsAmount,
      @JsonProperty("maxTextWordsAmount") Integer maxTextWordsAmount) {

    ValidationUtils.requireNotNull(
        vocabulary,
        String.format("'vocabulary' property cannot be null for %s field", VOCABULARY_TEXT_GENERATOR_JSON_TYPE));

    this.textGenerator = new TextGeneratorFactory().createTextGenerator(
        new VocabularyWordGenerator(new VocabularyGenerator<>(vocabulary)),
        wordsDelimiter,
        minTextWordsAmount,
        maxTextWordsAmount);
  }

  @Override
  public String generateValue() {
    return textGenerator.generateText();
  }

}
