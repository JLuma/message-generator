package ru.sbt.tengri.json.message.generator.config;

public class ConfigurationException extends RuntimeException {

  public ConfigurationException() {
  }

  public ConfigurationException(String message) {
    super(message);
  }

  public ConfigurationException(String message, Throwable cause) {
    super(message, cause);
  }

}
