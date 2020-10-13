package ru.sbt.tengri.kafka.message.generator;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Function;

public final class ConfigurationUtils {

    private ConfigurationUtils() {

    }

    public static String getTargetTopicProperty() {
        return getMandatoryProperty("kafka.topic");
    }

    public static String getKafkaServersProperty() {
        return getMandatoryProperty("kafka.bootstrap.servers");
    }

    public static long getMessagesNumberProperty() {
        return getLongPropertyOrDefault("kafka.messages.amount", 10L);
    }

    public static int getThreadsNumberProperty() {
        return getIntPropertyOrDefault("kafka.threads", Runtime.getRuntime().availableProcessors());
    }

    public static URI getMessageSchemaFileUri() {
        return getPathOrEmpty("message.schema.file.path").map(Path::toUri).orElseGet(ConfigurationUtils::getDefaultSchemaUri);
    }

    public static long getThrottlingDelay() {
        return getLongPropertyOrDefault("throttling.delay.millis", 0);
    }

    private static String getMandatoryProperty(String propertyName) {
        String propertyValue = System.getProperty(propertyName);
        if (propertyValue == null) {
            throw new IllegalArgumentException("[" + propertyName + "] property cannot be null");
        }
        return propertyValue;
    }

    private static Integer getIntPropertyOrDefault(String propertyName, int defaultValue) {
        return getPropertyOrDefault(propertyName, Integer::parseInt, defaultValue);
    }

    private static Long getLongPropertyOrDefault(String propertyName, long defaultValue) {
        return getPropertyOrDefault(propertyName, Long::parseLong, defaultValue);
    }

    private static <T> T getPropertyOrDefault(String propertyName, Function<String, T> castFn, T defaultValue) {
        String propertyValue = System.getProperty(propertyName);
        if (propertyValue == null) {
            return defaultValue;
        } else {
            try {
                return castFn.apply(propertyValue);
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException(
                    String.format("Cannot parse [%s] property. Should be numeric", propertyName),
                    ex);
            }
        }
    }

    private static URI getDefaultSchemaUri() {
        try {
            return ConfigurationUtils.class.getResource("/default-schema.json").toURI();
        } catch (URISyntaxException ex) {
            throw new IllegalStateException("Jar is corrupted. Cannot load default-schema.json");
        }
    }

    private static Optional<Path> getPathOrEmpty(String name) {
        String value = System.getProperty(name);
        if (value == null) {
            return Optional.empty();
        } else {
            return Optional.of(Paths.get(value));
        }
    }

}
