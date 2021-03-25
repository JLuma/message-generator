package org.jluma.message.generator.schema_based.text.word;

import org.jluma.message.generator.schema_based.field.value.generator.VocabularyGenerator;

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
