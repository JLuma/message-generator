package org.jluma.tools.loadtesting.syntheticdatagenerator.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Properties for Kafka producer and application-specific Kafka settings.
 */
@Data
@ConfigurationProperties(prefix = "kafka")
public class KafkaProducerProperties {

    /**
     * Kafka topic to send messages to
     */
    private String topic = "messages";

    /**
     * Total number of messages to send
     */
    private Long messagesAmount = 10L;

    /**
     * Delay in milliseconds between sending messages
     */
    private Long sendThrottlingDelayMillis = 0L;

    /**
     * Number of threads for sending messages
     */
    private Integer threads = Runtime.getRuntime().availableProcessors();

    /**
     * Client ID for the Kafka producer
     */
    private String clientId = "KafkaMessageGenerator";

    /**
     * Bootstrap servers for Kafka
     */
    private String bootstrapServers = "localhost:9092";
}
