package org.jluma.message.generator.schema_based.field.value.generator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jluma.message.generator.schema_based.utils.ConfigUtils;
import org.jluma.message.generator.schema_based.utils.RandomUtils;
import org.jluma.message.generator.schema_based.utils.Range;

public class DoubleValueGenerator implements FieldValueGenerator<Double> {

  public static final String DOUBLE_VALUE_GENERATOR_JSON_TYPE = "random_double";

  @JsonIgnore
  private final RandomUtils randomUtils = new RandomUtils();

  private final double minValueBound;
  private final double maxValueBound;

  public DoubleValueGenerator(
      @JsonProperty("minValue") double minValueBound,
      @JsonProperty("maxValue") double maxValueBound) {

    this.minValueBound = ConfigUtils.getDefaultIfNull(minValueBound, ConfigDefaults.DEFAULT_MIN_VALUE_BOUND);
    this.maxValueBound = ConfigUtils.getDefaultIfNull(maxValueBound, ConfigDefaults.DEFAULT_MAX_VALUE_BOUND);
  }

  @Override
  public Double generateValue() {
    return randomUtils.getRandomDoubleValue(Range.of(minValueBound, maxValueBound));
  }

  private static class ConfigDefaults {

    private static final double DEFAULT_MIN_VALUE_BOUND = 0.0;
    private static final double DEFAULT_MAX_VALUE_BOUND = 1.0;

  }

}
