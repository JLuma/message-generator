package ru.sbt.tengri.kafka.message.generator.proxy;

import ru.sbt.tengri.json.message.generator.MessageGenerator;

public class MessageGeneratorProxyImpl implements MessageGeneratorProxy {

  private final MessageGenerator messageGenerator;

  public MessageGeneratorProxyImpl(MessageGenerator messageGenerator) {
    this.messageGenerator = messageGenerator;
  }

  @Override
  public String generateMessage() {
    return messageGenerator.generateMessage();
  }

}
