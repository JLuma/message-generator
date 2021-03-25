package org.jluma.message.generator.schema_based.utils;

import java.util.function.Function;

public final class ConfigUtils {

  private ConfigUtils() {

  }

  public static <T> T getDefaultIfNull(T value, T defaultValue) {
    return getDefaultIfNullWithCast(value, Function.identity(), defaultValue);
  }

  public static <T, V> T getDefaultIfNullWithCast(V value, Function<V, T> castFn, T defaultValue) {
    return value == null ? defaultValue : castFn.apply(value);
  }

}
