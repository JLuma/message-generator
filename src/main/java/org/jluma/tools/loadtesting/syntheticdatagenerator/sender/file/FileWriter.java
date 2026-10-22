package org.jluma.tools.loadtesting.syntheticdatagenerator.sender.file;

import lombok.RequiredArgsConstructor;
import org.jluma.tools.loadtesting.syntheticdatagenerator.api.MessageSender;
import org.jluma.tools.loadtesting.syntheticdatagenerator.config.SenderProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;

@Component
@ConditionalOnProperty(name = "sender.type", havingValue = "file", matchIfMissing = true)
@RequiredArgsConstructor
public class FileWriter implements MessageSender {

    private final SenderProperties properties;

    private FileOutputStream os;

    @Override
    public void initialize() {
        try {
            this.os = new FileOutputStream(properties.getFile().getPath().toFile());
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void sendMessage(String message) {
        try {
            os.write(message.getBytes());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void close() {
        try {
            os.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
