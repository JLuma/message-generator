package ru.sbt.tengri.json.message.generator.field.value.generator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Value;
import ru.sbt.tengri.json.message.generator.utils.RandomUtils;

@Value
public class VocabularyGenerator<T> implements FieldValueGenerator<T> {

  public static final String VOCABULARY_GENERATOR_JSON_TYPE = "vocabulary";

  private static final RandomUtils rndUtils = new RandomUtils();

  List<T> vocabulary;

  @JsonCreator
  public VocabularyGenerator(@JsonProperty("vocabulary") List<T> vocabulary) {
    this.vocabulary = vocabulary;
  }

  @Override
  public T generateValue() {
    return rndUtils.getRandomValueFromList(vocabulary);
  }

}
