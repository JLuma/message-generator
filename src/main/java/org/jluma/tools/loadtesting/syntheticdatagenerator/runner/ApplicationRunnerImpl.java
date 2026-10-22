package org.jluma.tools.loadtesting.syntheticdatagenerator.runner;

import org.jluma.tools.loadtesting.syntheticdatagenerator.api.MessageGenerationException;
import org.jluma.tools.loadtesting.syntheticdatagenerator.api.MessageGenerator;
import org.jluma.tools.loadtesting.syntheticdatagenerator.api.MessageGeneratorInitializationException;
import org.jluma.tools.loadtesting.syntheticdatagenerator.config.KafkaProducerProperties;
import org.jluma.tools.loadtesting.syntheticdatagenerator.service.MessageGeneratorService;
import org.jluma.tools.loadtesting.syntheticdatagenerator.writer.MessageWriterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Application runner that orchestrates message generation and writing.
 */
@Component
public class ApplicationRunnerImpl implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationRunnerImpl.class);

    private final MessageGeneratorService messageGeneratorService;
    private final MessageWriterService messageWriterService;
    private final KafkaProducerProperties kafkaProperties;

    public ApplicationRunnerImpl(
            MessageGeneratorService messageGeneratorService,
            MessageWriterService messageWriterService,
            KafkaProducerProperties kafkaProperties) {
        this.messageGeneratorService = messageGeneratorService;
        this.messageWriterService = messageWriterService;
        this.kafkaProperties = kafkaProperties;
    }

    @Override
    public void run(String... args) throws Exception {
        LOG.info("=== Message Generator Application Started ===");

        // Initialize message generator
        messageGeneratorService.initialize();
        LOG.info("Message generator initialized successfully");

        if (messageGeneratorService.isSampleMode()) {
            LOG.info("Running in sample mode");
            writeSample();
        } else {
            LOG.info("Running in production mode - sending messages to Kafka");
            messageWriterService.writeMessages(
                new MessageGenerator() {
                    @Override
                    public void initialize() throws MessageGeneratorInitializationException {
                        // Already initialized
                    }

                    @Override
                    public String generateMessage() throws MessageGenerationException {
                        try {
                            return messageGeneratorService.generateMessage();
                        } catch (Exception e) {
                            throw new MessageGenerationException("Failed to generate message", e);
                        }
                    }
                }
            );
        }

        LOG.info("=== Message Generator Application Finished ===");
    }

    private void writeSample() throws IOException, MessageGenerationException {
        String sampleMessage = null;
        try {
            sampleMessage = messageGeneratorService.generateMessage();
        } catch (Exception e) {
            throw new MessageGenerationException("Failed to generate sample message", e);
        }

        String outputFile = "./sample.json";
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            fos.write(sampleMessage.getBytes());
        }
        
        System.out.println("Sample message written to " + outputFile);
        System.out.println(sampleMessage);
    }
}
