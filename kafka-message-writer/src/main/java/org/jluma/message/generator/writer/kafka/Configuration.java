package org.jluma.message.generator.writer.kafka;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

public final class Configuration {

    private final Properties props;

    Configuration(Properties properties) {
      this.props = properties;
    }

    public boolean isSampleMode() {
        return getPropertyOrDefault("sample-mode", Boolean::parseBoolean, false, "boolean");
    }

    public URI getConstantFileMessageUri() {
        return getPathOrEmpty("message.gen.constant-file.message-file-path")
                .map(Path::toUri)
                .orElseThrow(() -> new IllegalArgumentException("[message.gen.constant-file.message-file-path] property cannot be null"));
    }

    public String getTargetTopicProperty() {
        return getMandatoryProperty("kafka.topic");
    }

    public long getThrottlingDelay() {
        return getLongPropertyOrDefault("kafka.send.throttling.delay.millis", 0);
    }

    public String getKafkaServersProperty() {
        return getMandatoryProperty("kafka.bootstrap.servers");
    }

    public long getMessagesNumberProperty() {
        return getLongPropertyOrDefault("kafka.messages.amount", 10L);
    }

    public int getThreadsNumberProperty() {
        return getIntPropertyOrDefault("kafka.threads", Runtime.getRuntime().availableProcessors());
    }

    public URI getMessageSchemaFileUri() {
        return getPathOrEmpty("message.gen.schema-based.schema-file-path").map(Path::toUri).orElseGet(this::getDefaultSchemaUri);
    }

    public Properties getConfAsProperties() {
        Properties props = new Properties();
        props.putAll(this.props);
        return props;
    }

    public MessageGeneratorType getMessageGenerationMode() {
        return this.getPropertyOrDefault(
                "message.gen.mode",
                MessageGeneratorType::valueOf,
                MessageGeneratorType.SCHEMA_BASED,
                String.format("one of [%s]", Arrays.asList(MessageGeneratorType.values())));
    }

    private String getMandatoryProperty(String propertyName) {
        String propertyValue = props.getProperty(propertyName);
        if (propertyValue == null) {
            throw new IllegalArgumentException("[" + propertyName + "] property cannot be null");
        }
        return propertyValue;
    }

    private Integer getIntPropertyOrDefault(String propertyName, int defaultValue) {
        return getNumericPropertyOrDefault(propertyName, Integer::parseInt, defaultValue);
    }

    private Long getLongPropertyOrDefault(String propertyName, long defaultValue) {
        return getNumericPropertyOrDefault(propertyName, Long::parseLong, defaultValue);
    }

    private <T> T getNumericPropertyOrDefault(String propertyName, Function<String, T> castFn, T defaultValue) {
        return this.getPropertyOrDefault(propertyName, castFn, defaultValue, "numeric");
    }

    private <T> T getPropertyOrDefault(
            String propertyName,
            Function<String, T> castFn,
            T defaultValue,
            String typeAdvice) {

        String propertyValue = props.getProperty(propertyName);
        if (propertyValue == null) {
            return defaultValue;
        } else {
            try {
                return castFn.apply(propertyValue);
            } catch (Exception ex) {
                throw new IllegalArgumentException(
                        String.format("Cannot parse [%s] property. Should be %s", propertyName, typeAdvice),
                        ex);
            }
        }
    }

    private URI getDefaultSchemaUri() {
        try {
            return Configuration.class.getResource("/default-schema.json").toURI();
        } catch (URISyntaxException ex) {
            throw new IllegalStateException("Jar is corrupted. Cannot load default-schema.json");
        }
    }

    private Optional<Path> getPathOrEmpty(String name) {
        String value = props.getProperty(name);
        if (value == null) {
            return Optional.empty();
        } else {
            return Optional.of(Paths.get(value));
        }
    }

    public enum MessageGeneratorType {
        CONSTANT_FILE, SCHEMA_BASED
    }

}
