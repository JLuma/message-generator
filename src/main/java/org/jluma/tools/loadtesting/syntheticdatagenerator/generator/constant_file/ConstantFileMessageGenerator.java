package org.jluma.tools.loadtesting.syntheticdatagenerator.generator.constant_file;

import org.jluma.tools.loadtesting.syntheticdatagenerator.api.MessageGenerationException;
import org.jluma.tools.loadtesting.syntheticdatagenerator.api.MessageGenerator;
import org.jluma.tools.loadtesting.syntheticdatagenerator.api.MessageGeneratorInitializationException;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConstantFileMessageGenerator implements MessageGenerator {

  private final URI messageFileUri;
  private String messageAsString;

  public ConstantFileMessageGenerator(URI messageFileUri) {
    this.messageFileUri = messageFileUri;
  }

  @Override
  public void initialize() throws MessageGeneratorInitializationException {
    if (messageFileUri == null) {
      throw new MessageGeneratorInitializationException("Message file URI cannot be null for CONSTANT_FILE mode");
    }
    Path messageFilePath = Paths.get(messageFileUri);
    this.messageAsString = readMessageToString(messageFilePath);
  }

  @Override
  public String generateMessage() throws MessageGenerationException {
    return messageAsString;
  }

  private String readMessageToString(Path messageFilePath) throws MessageGeneratorInitializationException {
    try {
      return new String(Files.readAllBytes(messageFilePath));
    } catch (IOException e) {
      throw new MessageGeneratorInitializationException(String.format("Cannot read message from file [%s]", messageFilePath), e);
    }
  }

}
