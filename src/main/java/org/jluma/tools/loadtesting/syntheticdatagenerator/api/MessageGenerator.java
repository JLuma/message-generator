package org.jluma.tools.loadtesting.syntheticdatagenerator.api;

public interface MessageGenerator {

    void initialize() throws MessageGeneratorInitializationException;
    String generateMessage() throws MessageGenerationException;

}
