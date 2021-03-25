package org.jluma.message.generator.constant_file;

import org.jluma.message.generator.api.MessageGenerationException;
import org.jluma.message.generator.api.MessageGenerator;
import org.jluma.message.generator.api.MessageGeneratorInitializationException;

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
