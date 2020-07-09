package ru.sbt.tengri.json.message.generator.json.field.value.generator;

import java.util.Random;

public class IntValueGenerator implements FieldValueGenerator<Integer> {

    private final Random rnd = new Random();
    private static final int MAX_GENERATED_INT_BOUND = 1_000_000;

    @Override
    public Integer generateValue() {
        return rnd.nextInt(MAX_GENERATED_INT_BOUND);
    }

}
