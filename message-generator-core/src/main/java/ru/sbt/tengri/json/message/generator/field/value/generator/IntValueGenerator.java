package ru.sbt.tengri.json.message.generator.field.value.generator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.sbt.tengri.json.message.generator.utils.ConfigUtils;
import ru.sbt.tengri.json.message.generator.utils.RandomUtils;
import ru.sbt.tengri.json.message.generator.utils.Range;

public class IntValueGenerator implements FieldValueGenerator<Integer> {

    public static final String RANDOM_INT_GENERATOR_JSON_TYPE = "random_int";

    @JsonIgnore
    private final RandomUtils randomUtils = new RandomUtils();

    private final int minValueBound;
    private final int maxValueBound;

    @JsonCreator
    public IntValueGenerator(
        @JsonProperty("minValue") Integer minValueBound,
        @JsonProperty("maxValue") Integer maxValueBound) {

        this.minValueBound = ConfigUtils.getDefaultIfNull(minValueBound, ConfigDefaults.DEFAULT_MIN_VALUE_BOUND);
        this.maxValueBound = ConfigUtils.getDefaultIfNull(maxValueBound, ConfigDefaults.DEFAULT_MAX_VALUE_BOUND);
    }

    @Override
    public Integer generateValue() {
        return randomUtils.genRandomValueAtRange(Range.of(minValueBound, maxValueBound));
    }

    private static class ConfigDefaults {
        private static final int DEFAULT_MIN_VALUE_BOUND = 0;
        private static final int DEFAULT_MAX_VALUE_BOUND = 1_000_000;
    }

}
