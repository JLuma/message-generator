package ru.sbt.tengri.json.message.generator.json.field.value.generator;

import ru.sbt.tengri.json.message.generator.text.TextGenerator;

public class StringValueGenerator implements FieldValueGenerator<String> {

    private final TextGenerator textGenerator;

    public StringValueGenerator(TextGenerator textGenerator) {
        this.textGenerator = textGenerator;
    }

    @Override
    public String generateValue() {
        return textGenerator.generateText();
    }

}
