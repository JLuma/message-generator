package ru.sbt.tengri.json.message.generator.text.word;

import ru.sbt.tengri.json.message.generator.field.value.generator.VocabularyGenerator;

public class VocabularyWordGenerator implements WordGenerator {

  private final VocabularyGenerator<String> vocabularyGenerator;

  public VocabularyWordGenerator(VocabularyGenerator<String> vocabularyGenerator) {
    this.vocabularyGenerator = vocabularyGenerator;
  }

  @Override
  public String generateWord() {
    return vocabularyGenerator.generateValue();
  }

}
