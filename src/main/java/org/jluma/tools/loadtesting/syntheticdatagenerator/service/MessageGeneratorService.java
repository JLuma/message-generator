package org.jluma.tools.loadtesting.syntheticdatagenerator.service;

import org.jluma.tools.loadtesting.syntheticdatagenerator.config.MessageGeneratorProperties;
import org.jluma.tools.loadtesting.syntheticdatagenerator.config.MessageGeneratorProperties.MessageGenerationMode;
import org.jluma.tools.loadtesting.syntheticdatagenerator.generator.MessageGeneratorFactory;
import org.jluma.tools.loadtesting.syntheticdatagenerator.api.MessageGenerator;
import org.jluma.tools.loadtesting.syntheticdatagenerator.api.MessageGeneratorInitializationException;
import org.springframework.stereotype.Service;

/**
 * Service that manages message generation based on configuration.
 */
@Service
public class MessageGeneratorService {

    private final MessageGeneratorProperties properties;
    private final MessageGeneratorFactory messageGeneratorFactory;
    private MessageGenerator messageGenerator;

    public MessageGeneratorService(
            MessageGeneratorProperties properties,
            MessageGeneratorFactory messageGeneratorFactory) {
        this.properties = properties;
        this.messageGeneratorFactory = messageGeneratorFactory;
    }

    /**
     * Initialize the appropriate message generator based on configuration.
     */
    public void initialize() throws MessageGeneratorInitializationException {
        MessageGenerationMode mode = properties.getMode();
        
        switch (mode) {
            case SCHEMA_BASED -> {
                var schemaPath = properties.getSchemaBased().getSchemaFilePath();
                messageGenerator = messageGeneratorFactory.createSchemaBasedGenerator(schemaPath);
            }
            case CONSTANT_FILE -> {
                var filePath = properties.getConstantFile().getMessageFilePath();
                messageGenerator = messageGeneratorFactory.createConstantFileGenerator(filePath);
            }
            default -> throw new IllegalArgumentException(
                    "Unsupported message generation mode: " + mode);
        }
        
        messageGenerator.initialize();
    }

    /**
     * Generate a single message.
     */
    public String generateMessage() throws Exception {
        if (messageGenerator == null) {
            throw new IllegalStateException("MessageGenerator not initialized. Call initialize() first.");
        }
        return messageGenerator.generateMessage();
    }

    /**
     * Check if sample mode is enabled.
     */
    public boolean isSampleMode() {
        return properties.isSampleMode();
    }
}
