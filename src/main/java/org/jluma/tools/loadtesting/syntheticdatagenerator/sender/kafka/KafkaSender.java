package org.jluma.tools.loadtesting.syntheticdatagenerator.sender.kafka;

import lombok.RequiredArgsConstructor;
import org.jluma.tools.loadtesting.syntheticdatagenerator.api.MessageSender;
import org.jluma.tools.loadtesting.syntheticdatagenerator.config.SenderProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@ConditionalOnProperty(name = "sender.type", havingValue = "kafka")
@RequiredArgsConstructor
public class KafkaSender implements MessageSender {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaSender.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final SenderProperties senderProperties;

    /**
     * Write a single message to Kafka.
     */
    public void sendMessage(final String message) {
        final String topic = senderProperties.getKafka().getTopic();
        try {
            String key = UUID.randomUUID().toString();
            kafkaTemplate.send(topic, key, message);
        } catch (Exception e) {
            LOG.error("Failed to send message to Kafka topic: {}", topic, e);
            throw new RuntimeException("Failed to send message", e);
        }
    }
}
