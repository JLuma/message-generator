package org.jluma.message.generator.api;

public interface MessageGenerator {

    void initialize() throws MessageGeneratorInitializationException;
    String generateMessage() throws MessageGenerationException;

}
