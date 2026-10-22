package org.jluma.tools.loadtesting.syntheticdatagenerator.generator.constant;

import org.jluma.tools.loadtesting.syntheticdatagenerator.api.MessageGenerator;
import org.jluma.tools.loadtesting.syntheticdatagenerator.config.GeneratorProperties;
import org.jluma.tools.loadtesting.syntheticdatagenerator.generator.schema.schema.MessageSchemaLoader;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
@ConditionalOnProperty(name = "generator.mode", havingValue = "CONSTANT_FILE", matchIfMissing = true)
public class ConstantFileMessageGenerator implements MessageGenerator {

  private final String messageAsString;

  public ConstantFileMessageGenerator(GeneratorProperties properties) {
    final Path messageFilePath = properties.getConstantFile().getMessageFilePath();
    if (messageFilePath == null) {
      this.messageAsString = new String(getDefaultConstantFile());
    } else {
      this.messageAsString = readMessageToString(messageFilePath);
    }
  }

  @Override
  public String generateMessage() {
    return messageAsString;
  }

  private String readMessageToString(Path messageFilePath) {
    try {
      return new String(Files.readAllBytes(messageFilePath));
    } catch (IOException e) {
      throw new UncheckedIOException(String.format("Cannot read message from file [%s]", messageFilePath), e);
    }
  }

  private byte[] getDefaultConstantFile() {
    try (InputStream in = MessageSchemaLoader.class.getResourceAsStream("/sample.json")) {
      if (in == null) {
        throw new IllegalStateException("Jar is corrupted. Cannot find sample.json");
      }
      return in.readAllBytes();
    } catch (IOException ex) {
      throw new UncheckedIOException("Cannot read sample.json", ex);
    }
  }

}
