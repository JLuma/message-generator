package org.jluma.message.generator.schema_based.field.value.generator.factory;

import org.jluma.message.generator.schema_based.text.TextGenerator;
import org.jluma.message.generator.schema_based.text.word.WordGenerator;
import org.jluma.message.generator.schema_based.utils.ConfigUtils;
import org.jluma.message.generator.schema_based.utils.Range;

public class TextGeneratorFactory {

  private static final int DEFAULT_MIN_WORDS_AMOUNT = 1;
  private static final int DEFAULT_MAX_WORDS_AMOUNT = 7;

  private static final String DEFAULT_WORDS_DELIMITER = " ";

  public TextGenerator createTextGenerator(
      WordGenerator wordGenerator,
      String wordsDelimiter,
      Integer minTextWordsAmount,
      Integer maxTextWordsAmount) {

    String actualWordsDelimiter = ConfigUtils.getDefaultIfNull(wordsDelimiter, DEFAULT_WORDS_DELIMITER);
    int actualMinTextWordsAmount = ConfigUtils.getDefaultIfNull(minTextWordsAmount, DEFAULT_MIN_WORDS_AMOUNT);
    int actualMaxTextWordsAmount = ConfigUtils.getDefaultIfNull(maxTextWordsAmount, DEFAULT_MAX_WORDS_AMOUNT);
    return new TextGenerator(wordGenerator, actualWordsDelimiter, Range.of(actualMinTextWordsAmount, actualMaxTextWordsAmount));
  }

}
