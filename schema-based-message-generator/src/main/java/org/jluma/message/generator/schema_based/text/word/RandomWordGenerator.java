package org.jluma.message.generator.schema_based.text.word;

import org.apache.commons.lang3.RandomStringUtils;
import org.jluma.message.generator.schema_based.utils.Range;

public class RandomWordGenerator implements WordGenerator {

  private final Range<Integer> wordLengthRange;

  public RandomWordGenerator(Range<Integer> wordLengthRange) {
    this.wordLengthRange = wordLengthRange;
  }

  @Override
  public String generateWord() {
    return RandomStringUtils.randomAlphabetic(wordLengthRange.getMin(), wordLengthRange.getMax());
  }

}
