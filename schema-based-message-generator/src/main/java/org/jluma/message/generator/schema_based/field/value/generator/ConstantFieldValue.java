package org.jluma.message.generator.schema_based.field.value.generator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;
import org.jluma.message.generator.schema_based.utils.validation.ValidationUtils;

@Value
public class ConstantFieldValue<T> implements FieldValueGenerator<T> {

    public static final String CONSTANT_VALUE_GENERATOR_JSON_TYPE = "const";

    T value;

    @JsonCreator
    public ConstantFieldValue(@JsonProperty("value") T value) {
        ValidationUtils.requireNotNull(value, "'value' of constant field cannot be null!");
        this.value = value;
    }

    @Override
    public T generateValue() {
        return value;
    }

}
