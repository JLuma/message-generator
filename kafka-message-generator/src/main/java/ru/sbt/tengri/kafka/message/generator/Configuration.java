package ru.sbt.tengri.kafka.message.generator;

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

    public String getStaticMessagePath() {
        return getMandatoryProperty("static.message.file");
    }

    public String getTargetTopicProperty() {
        return getMandatoryProperty("kafka.topic");
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
        return getPathOrEmpty("message.schema.file.path").map(Path::toUri).orElseGet(this::getDefaultSchemaUri);
    }

    public MessageGenerationMode getMessageGenerationMode() {
        return this.getPropertyOrDefault(
                "message.gen.mode",
                MessageGenerationMode::valueOf,
                MessageGenerationMode.GENERATED,
                String.format("one of [%s]", Arrays.asList(MessageGenerationMode.values())));
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

    public enum MessageGenerationMode {
        STATIC, GENERATED
    }

}
