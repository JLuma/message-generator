package org.jluma.message.generator.api;

public class MessageGenerationException extends Exception {

    public MessageGenerationException(String message) {
        super(message);
    }

    public MessageGenerationException(String message, Throwable cause) {
        super(message, cause);
    }

}
