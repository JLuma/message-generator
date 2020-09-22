package ru.sbt.tengri.kafka.message.generator.proxy;

import ru.sbt.tengri.kafka.message.generator.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class StaticMessageGeneratorProxyFactory implements MessageGeneratorProxyFactory {

  private final String messageBody;

  public StaticMessageGeneratorProxyFactory(Configuration conf) {
    String staticMessageFilePathAsString = conf.getStaticMessagePath();
    try {
      this.messageBody = new String(Files.readAllBytes(Paths.get(staticMessageFilePathAsString)));
    } catch (IOException ex) {
      throw new IllegalArgumentException(
              String.format("Cannot load static message file from path [%s]", staticMessageFilePathAsString),
              ex);
    }
  }

  @Override
  public MessageGeneratorProxy createMessageGeneratorProxy() {
    return new StaticMessageGenerator(messageBody);
  }

}
