package ru.sbt.tengri.json.message.generator.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sbt.tengri.json.message.generator.text.Range;

public class JsonGeneratorConfig {

  private static final Logger LOG = LoggerFactory.getLogger(JsonGeneratorConfig.class);

  public static final String SCHEMA_FILE_PATH_PROPERTY_NAME = "json.schema.file";

  /*public static final String WORD_GENERATOR_TYPE_PROPERTY_NAME = "text.gen.word.genType";

  public static final String VOCABULARY_FILE_PATH_PROPERTY_NAME = "text.gen.vocabulary.vocabularyFile";

  public static final String WORD_LENGTH_RANGE_FROM_PROPERTY_NAME = "text.gen.random.wordLengthRange.from";
  public static final String WORD_LENGTH_RANGE_TO_PROPERTY_NAME = "text.gen.random.wordLengthRange.to";

  public static final String TEXT_WORDS_AMOUNT_RANGE_FROM_PROPERTY_NAME = "text.gen.wordsAmountRange.from";
  public static final String TEXT_WORDS_AMOUNT_RANGE_TO_PROPERTY_NAME = "text.gen.wordsAmountRange.to";*/

  private final Properties config;

  private JsonGeneratorConfig(Properties config) {
    this.config = config;
    this.config.putAll(loadSystemProperties());
    LOG.debug("Loaded config properties {}", config);
  }

  public static JsonGeneratorConfig defaults() {
    return new JsonGeneratorConfig(new Properties());
  }

  public static JsonGeneratorConfig load(String configurationFile) throws ConfigurationException {
    Path configurationFilePath = Paths.get(configurationFile);
    Properties properties = new Properties();
    try (InputStream is = new FileInputStream(configurationFilePath.toFile())) {
      properties.load(is);
    } catch (FileNotFoundException ex) {
      throw new ConfigurationException(String.format("Configuration file not found for path [%s]", configurationFilePath));
    } catch (IOException ex) {
      throw new ConfigurationException(String.format("Cannot load configuration file from path [%s]", configurationFile), ex);
    }
    return new JsonGeneratorConfig(properties);
  }

  private Properties loadSystemProperties() {
    return System.getProperties();
  }

  public URI getSchemaFileUri() {
    return getPathOrEmpty(SCHEMA_FILE_PATH_PROPERTY_NAME).map(Path::toUri).orElseGet(this::getDefaultSchemaUri);
  }

  private URI getDefaultSchemaUri() {
    try {
      return getClass().getResource("/default-schema.json").toURI();
    } catch (URISyntaxException ex) {
      throw new IllegalStateException("Jar is corrupted. Cannot load default-schema.json");
    }
  }

  public Range getTextWordsAmountRange() {
    int wordLengthRangeFrom = getPropertyOrDefault(TEXT_WORDS_AMOUNT_RANGE_FROM_PROPERTY_NAME, 1, Integer::parseInt);
    int wordLengthRangeTo = getPropertyOrDefault(TEXT_WORDS_AMOUNT_RANGE_TO_PROPERTY_NAME, 15, Integer::parseInt);
    return Range.of(wordLengthRangeFrom, wordLengthRangeTo);
  }

  public Range getRandomWordLengthRange() {
    int wordLengthRangeFrom = getPropertyOrDefault(WORD_LENGTH_RANGE_FROM_PROPERTY_NAME, 3, Integer::parseInt);
    int wordLengthRangeTo = getPropertyOrDefault(WORD_LENGTH_RANGE_TO_PROPERTY_NAME, 7, Integer::parseInt);
    return Range.of(wordLengthRangeFrom, wordLengthRangeTo);
  }

  public Optional<Path> getVocabularyFilePath() {
    return getPathOrEmpty(VOCABULARY_FILE_PATH_PROPERTY_NAME);
  }

  public WordGeneratorType getWordGeneratorType() throws ConfigurationException {
    try {
      return getPropertyOrDefault(WORD_GENERATOR_TYPE_PROPERTY_NAME, WordGeneratorType.RANDOM, WordGeneratorType::valueOf);
    } catch (IllegalArgumentException ex) {
      throw new ConfigurationException(
          String.format(
              "Illegal value for [%s] property. Must be on of [%s]",
              WORD_GENERATOR_TYPE_PROPERTY_NAME,
              Arrays.toString(WordGeneratorType.values())));
    }

  }

  private <T> T getPropertyOrDefault(String name, T defaultValue, Function<String, T> castFn) {
    String value = config.getProperty(name);
    if (value == null) {
      return defaultValue;
    } else {
      return castFn.apply(value);
    }
  }

  private Optional<Path> getPathOrEmpty(String name) {
    String value = config.getProperty(name);
    if (value == null) {
      return Optional.empty();
    } else {
      return Optional.of(Paths.get(value));
    }
  }

  public enum WordGeneratorType {
    VOCABULARY, RANDOM
  }

}
