package ru.sbt.tengri.json.message.generator.text;

import java.util.List;
import java.util.Random;

public class RandomUtils {

  private final Random rnd = new Random();

  public int genRandomValueAtRange(Range range) {
    if (range.getMin() == range.getMax()) {
      return range.getMin();
    } else {
      return range.getMin() + rnd.nextInt(range.getMax() - range.getMin() + 1);
    }
  }

  public <T> T getRandomValueFromList(List<T> list) {
    return list.get(rnd.nextInt(list.size()));
  }

}
