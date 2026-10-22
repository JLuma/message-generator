package org.jluma.tools.loadtesting.syntheticdatagenerator.generator.schema.text.word;

import org.jluma.tools.loadtesting.syntheticdatagenerator.generator.schema.field.value.generator.VocabularyGenerator;

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
