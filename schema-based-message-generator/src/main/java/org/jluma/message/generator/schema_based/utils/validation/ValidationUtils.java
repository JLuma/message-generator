package org.jluma.message.generator.schema_based.utils.validation;

public final class ValidationUtils {

  private ValidationUtils() {
  }

  public static void requireNotNull(Object value, String exceptionMessage) {
    if (value == null) throw new ValidationException(exceptionMessage);
  }

}
