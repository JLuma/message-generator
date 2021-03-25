package org.jluma.message.generator.schema_based.field.value.generator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.jluma.message.generator.schema_based.utils.ConfigUtils;
import org.jluma.message.generator.schema_based.utils.RandomUtils;
import org.jluma.message.generator.schema_based.utils.Range;

public class IncrementingTimeFieldGenerator implements FieldValueGenerator<String> {

  public static final String INCREMENTING_TIME_FIELD_GENERATOR_JSON_TYPE = "incrementing_time";

  private final String dateFormat;
  private final DateTimeFormatter dateFormatter;
  private LocalDateTime baseDate;

  private final int dateIncrement;
  private final int dateIncrementDelta;
  private final ChronoUnit dateIncrementUnit;

  @JsonIgnore
  private final RandomUtils randomUtils = new RandomUtils();

  @JsonCreator
  public IncrementingTimeFieldGenerator(
      @JsonProperty("dateFormat") String dateFormat,
      @JsonProperty("baseDate") String baseDate,
      @JsonProperty("dateIncrement") int dateIncrement,
      @JsonProperty("dateIncrementDelta") int dateIncrementDelta,
      @JsonProperty("dateIncrementUnit") String dateIncrementUnit) {

    this.dateFormat = dateFormat;
    this.dateFormatter = ConfigUtils.getDefaultIfNullWithCast(
        dateFormat,
        this::createDateFormatter,
        ConfigDefaults.DEFAULT_DATE_FORMATTER);

    this.baseDate = ConfigUtils.getDefaultIfNullWithCast(
        baseDate,
        dateAsString -> LocalDateTime.parse(dateAsString, this.dateFormatter),
        ConfigDefaults.DEFAULT_BASE_DATE);

    this.dateIncrement = ConfigUtils.getDefaultIfNull(dateIncrement, ConfigDefaults.DEFAULT_DATE_INCREMENT);
    this.dateIncrementDelta = ConfigUtils.getDefaultIfNull(dateIncrementDelta, ConfigDefaults.DEFAULT_DATE_INCREMENT_DELTA);

    this.dateIncrementUnit = ConfigUtils.getDefaultIfNullWithCast(
        dateIncrementUnit,
        this::parseChronoUnit,
        ConfigDefaults.DEFAULT_DATE_INCREMENT_UNIT);
  }

  @Override
  public String generateValue() {
    LocalDateTime targetDate = baseDate;

    if (dateIncrement != 0) {
      int targetDateIncrement = randomUtils.genRandomValueAtRange(Range.of(
          dateIncrement - dateIncrementDelta,
          dateIncrement + dateIncrementDelta));

      targetDate = targetDate.plus(targetDateIncrement, dateIncrementUnit);
    }

    this.baseDate = targetDate;
    try {
      return targetDate.format(dateFormatter);
    } catch (DateTimeException ex) {
      throw new IllegalArgumentException(
          String.format("Cannot format date [%s] to format [%s]. Invalid format?", targetDate, dateFormat),
          ex);
    }
  }

  private ChronoUnit parseChronoUnit(String timeUnit) {
    try {
      return ChronoUnit.valueOf(timeUnit);
    } catch (IllegalArgumentException ex) {
      throw new IllegalArgumentException(
          String.format(
              "Unknown time unit [%s]. Allowed values [%s]",
              timeUnit,
              Arrays.asList(TimeUnit.values())));
    }
  }

  private DateTimeFormatter createDateFormatter(String dateFormat) {
    try {
      return DateTimeFormatter.ofPattern(dateFormat);
    } catch (IllegalArgumentException ex) {
      throw new IllegalArgumentException("Invalid date format", ex);
    }
  }

  private static class ConfigDefaults {

    private static final DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;
    private static final LocalDateTime DEFAULT_BASE_DATE = LocalDateTime.now();
    private static final int DEFAULT_DATE_INCREMENT = 100;
    private static final int DEFAULT_DATE_INCREMENT_DELTA = 15;
    private static final ChronoUnit DEFAULT_DATE_INCREMENT_UNIT = ChronoUnit.MILLIS;

  }

}
