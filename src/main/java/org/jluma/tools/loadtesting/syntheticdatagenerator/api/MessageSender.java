package org.jluma.tools.loadtesting.syntheticdatagenerator.api;

import java.io.Closeable;

public interface MessageSender extends Closeable {

    void sendMessage(String message);

    default void initialize() {

    }

    default void close() {

    }

    default boolean isMultiThreadSupported() {
        return false;
    }

}
