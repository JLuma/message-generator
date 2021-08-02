package org.jluma.message.generator.schema_based.utils;

import java.util.List;
import java.util.Random;

public final class RandomUtils {

  private final Random rnd = new Random();

  public int genRandomIntegerValueAtRange(Range<Integer> range) {
    if (range.getMin() == range.getMax()) {
      return range.getMin();
    } else {
      return range.getMin() + rnd.nextInt(range.getMax() - range.getMin() + 1);
    }
  }

  public boolean getRandomBooleanValue() {
    return rnd.nextBoolean();
  }

  public double getRandomDoubleValue(Range<Double> range) {
    if (range.getMin() == range.getMax()) {
      return range.getMin();
    } else {
      return range.getMin() + rnd.nextDouble() * (range.getMax() - range.getMin());
    }
  }

  public <T> T getRandomValueFromList(List<T> list) {
    return list.get(rnd.nextInt(list.size()));
  }

}
