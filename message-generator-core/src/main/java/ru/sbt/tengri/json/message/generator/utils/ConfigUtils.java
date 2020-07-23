package ru.sbt.tengri.json.message.generator.utils;

public final class ConfigUtils {

  private ConfigUtils() {

  }

  public static <T> T getDefaultIfNull(T value, T defaultValue) {
    return value == null ? defaultValue : value;
  }

}
