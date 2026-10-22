package org.jluma.tools.loadtesting.syntheticdatagenerator.api;

public interface Flow {

    void generateAndSendMessages(MessageGenerator generator, MessageSender sender);

}
