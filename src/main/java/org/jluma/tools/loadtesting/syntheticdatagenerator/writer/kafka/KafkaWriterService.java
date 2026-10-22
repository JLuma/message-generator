package org.jluma.tools.loadtesting.syntheticdatagenerator.writer.kafka;

import org.jluma.tools.loadtesting.syntheticdatagenerator.api.MessageGenerator;
import org.jluma.tools.loadtesting.syntheticdatagenerator.config.KafkaProducerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Spring Kafka service for writing messages.
 */
@Service
public class KafkaWriterService {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaWriterService.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaProducerProperties kafkaProperties;

    public KafkaWriterService(KafkaTemplate<String, String> kafkaTemplate,
                              KafkaProducerProperties kafkaProperties) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaProperties = kafkaProperties;
    }

    /**
     * Write a single message to Kafka.
     */
    public void writeMessage(MessageGenerator messageGenerator) {
        try {
            String message = messageGenerator.generateMessage();
            String key = UUID.randomUUID().toString();
            kafkaTemplate.send(kafkaProperties.getTopic(), key, message);
        } catch (Exception e) {
            LOG.error("Failed to send message to Kafka topic: {}", kafkaProperties.getTopic(), e);
            throw new RuntimeException("Failed to send message", e);
        }
    }
}
