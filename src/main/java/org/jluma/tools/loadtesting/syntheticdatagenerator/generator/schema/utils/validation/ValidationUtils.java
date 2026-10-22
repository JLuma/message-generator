package org.jluma.tools.loadtesting.syntheticdatagenerator.generator.schema.utils.validation;

public final class ValidationUtils {

  private ValidationUtils() {
  }

  public static void requireNotNull(Object value, String exceptionMessage) {
    if (value == null) throw new ValidationException(exceptionMessage);
  }

}
