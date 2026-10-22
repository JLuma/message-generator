package org.jluma.tools.loadtesting.syntheticdatagenerator.generator.schema.text.word;

import org.apache.commons.text.RandomStringGenerator;
import org.jluma.tools.loadtesting.syntheticdatagenerator.generator.schema.utils.Range;

public class RandomWordGenerator implements WordGenerator {

  private final Range wordLengthRange;
  private final RandomStringGenerator generator;

  public RandomWordGenerator(Range wordLengthRange) {
    this.wordLengthRange = wordLengthRange;
    this.generator = RandomStringGenerator.builder().withinRange('a', 'z').get();
  }

  @Override
  public String generateWord() {
    return generator.generate(wordLengthRange.getMin(), wordLengthRange.getMax());
  }

}
