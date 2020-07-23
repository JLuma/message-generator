package ru.sbt.tengri.json.message.generator.field.value.generator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.sbt.tengri.json.message.generator.field.value.generator.factory.TextGeneratorFactory;
import ru.sbt.tengri.json.message.generator.field.value.generator.factory.WordGeneratorFactory;
import ru.sbt.tengri.json.message.generator.text.word.WordGenerator;
import ru.sbt.tengri.json.message.generator.text.TextGenerator;

public class RandomTextGenerator implements FieldValueGenerator<String> {

  public static final String RANDOM_TEXT_GENERATOR_JSON_TYPE = "random_text";

  @JsonIgnore
  private final TextGenerator textGenerator;

  public RandomTextGenerator(
      @JsonProperty("minWordLength") Integer minWordLength,
      @JsonProperty("maxWordLength") Integer maxWordLength,
      @JsonProperty("minTextWordsAmount") Integer minTextWordsAmount,
      @JsonProperty("maxTextWordsAmount") Integer maxTextWordsAmount,
      @JsonProperty("wordsDelimiter") String wordsDelimiter) {

    WordGenerator wordGenerator = new WordGeneratorFactory().createRandomWordGenerator(minWordLength, maxWordLength);
    this.textGenerator = new TextGeneratorFactory().createTextGenerator(
        wordGenerator,
        wordsDelimiter,
        minTextWordsAmount,
        maxTextWordsAmount);
  }

  @Override
  public String generateValue() {
    return textGenerator.generateText();
  }

}
