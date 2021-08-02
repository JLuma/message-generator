package org.jluma.message.generator.schema_based.utils;

public class Range<T> {

  private final T min;
  private final T max;

  private Range(T min, T max) {
    this.min = min;
    this.max = max;
  }

  public static Range<Integer> of(int min, int max) {
    if (max < min) {
      throw new IllegalArgumentException(String.format("Max [%s] value must be more than min [%s]", max, min));
    }
    return new Range<>(min, max);
  }

  public static Range<Double> of(double min, double max) {
    if (max < min) {
      throw new IllegalArgumentException(String.format("Max [%s] value must be more than min [%s]", max, min));
    }
    return new Range<>(min, max);
  }

  public static Range<Integer> ofMax(int max) {
    return Range.of(0, max);
  }

  public T getMin() {
    return min;
  }

  public T getMax() {
    return max;
  }

}
