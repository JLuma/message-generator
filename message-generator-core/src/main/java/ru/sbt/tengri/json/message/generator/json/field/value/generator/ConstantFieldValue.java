package ru.sbt.tengri.json.message.generator.json.field.value.generator;

public class ConstantFieldValue<T> implements FieldValueGenerator<T> {

    private final T value;

    public ConstantFieldValue(T value) {
        this.value = value;
    }

    @Override
    public T generateValue() {
        return value;
    }

}
