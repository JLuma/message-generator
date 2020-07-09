package ru.sbt.tengri.kafka.message.generator;

public final class ConfigurationUtils {

    private ConfigurationUtils() {

    }

    public static String getTargetTopicProperty() {
        return getMandatoryProperty("kafka.topic");
    }

    public static String getKafkaServersProperty() {
        return getMandatoryProperty("kafka.bootstrap.servers");
    }

    public static int getMessagesNumberProperty() {
        return getIntPropertyOrDefault("kafka.messages.amount", 100);
    }

    public static int getThreadsNumberProperty() {
        return getIntPropertyOrDefault("kafka.threads", Runtime.getRuntime().availableProcessors());
    }

    private static String getMandatoryProperty(String propertyName) {
        String propertyValue = System.getProperty(propertyName);
        if (propertyValue == null) {
            throw new IllegalArgumentException("[" + propertyName + "] property cannot be null");
        }
        return propertyValue;
    }

    private static Integer getIntPropertyOrDefault(String propertyName, int defaultValue) {
        String propertyValue = System.getProperty(propertyName);
        if (propertyValue == null) {
            return defaultValue;
        } else {
            try {
                return Integer.parseInt(propertyValue);
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException(
                        String.format("Cannot parse [%s] property. Should be numeric", propertyName),
                        ex);
            }
        }
    }

}
