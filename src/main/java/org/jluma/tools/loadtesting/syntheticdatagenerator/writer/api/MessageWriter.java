package org.jluma.tools.loadtesting.syntheticdatagenerator.writer.api;

import org.jluma.tools.loadtesting.syntheticdatagenerator.api.MessageGenerator;

public interface MessageWriter {

    void writeMessages(MessageGenerator messageGenerator) throws InterruptedException;

}
