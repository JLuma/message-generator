package ru.sbt.tengri.json.message.generator.text.word;

import org.apache.commons.lang3.RandomStringUtils;
import ru.sbt.tengri.json.message.generator.utils.Range;

public class RandomWordGenerator implements WordGenerator {

  private final Range wordLengthRange;

  public RandomWordGenerator(Range wordLengthRange) {
    this.wordLengthRange = wordLengthRange;
  }

  @Override
  public String generateWord() {
    return RandomStringUtils.randomAlphabetic(wordLengthRange.getMin(), wordLengthRange.getMax());
  }

}
