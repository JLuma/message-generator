package ru.sbt.tengri.json.message.generator.field.value.generator.factory;

import ru.sbt.tengri.json.message.generator.text.word.RandomWordGenerator;
import ru.sbt.tengri.json.message.generator.text.word.WordGenerator;
import ru.sbt.tengri.json.message.generator.utils.ConfigUtils;
import ru.sbt.tengri.json.message.generator.utils.Range;

public class WordGeneratorFactory {

  private static final int DEFAULT_MIN_WORD_LENGTH = 3;
  private static final int DEFAULT_MAX_WORD_LENGTH = 12;

  public WordGenerator createRandomWordGenerator(Integer minWordLength, Integer maxWordLength) {
    int actualMinWordLength = ConfigUtils.getDefaultIfNull(minWordLength, DEFAULT_MIN_WORD_LENGTH);
    int actualMaxWordLength = ConfigUtils.getDefaultIfNull(maxWordLength, DEFAULT_MAX_WORD_LENGTH);

    return new RandomWordGenerator(Range.of(actualMinWordLength, actualMaxWordLength));
  }

}
