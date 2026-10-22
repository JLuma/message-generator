package org.jluma.tools.loadtesting.syntheticdatagenerator.generator.schema.field.value.generator.factory;

import org.jluma.tools.loadtesting.syntheticdatagenerator.generator.schema.text.word.RandomWordGenerator;
import org.jluma.tools.loadtesting.syntheticdatagenerator.generator.schema.text.word.WordGenerator;
import org.jluma.tools.loadtesting.syntheticdatagenerator.generator.schema.utils.Range;
import org.jluma.tools.loadtesting.syntheticdatagenerator.generator.schema.utils.ConfigUtils;

public class WordGeneratorFactory {

  private static final int DEFAULT_MIN_WORD_LENGTH = 3;
  private static final int DEFAULT_MAX_WORD_LENGTH = 12;

  public WordGenerator createRandomWordGenerator(Integer minWordLength, Integer maxWordLength) {
    int actualMinWordLength = ConfigUtils.getDefaultIfNull(minWordLength, DEFAULT_MIN_WORD_LENGTH);
    int actualMaxWordLength = ConfigUtils.getDefaultIfNull(maxWordLength, DEFAULT_MAX_WORD_LENGTH);

    return new RandomWordGenerator(Range.of(actualMinWordLength, actualMaxWordLength));
  }

}
