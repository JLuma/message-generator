package ru.sbt.tengri.json.message.generator.text;

public class Range {

    private final int min;
    private final int max;

    private Range(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public static Range of(int min, int max) {
        if (max < min) {
            throw new IllegalArgumentException(String.format("Max [%s] value must be more than min [%s]", max, min));
        }
        return new Range(min, max);
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

}
