package ru.sbt.tengri.kafka.message.generator.proxy;

public class StaticMessageGenerator implements MessageGeneratorProxy {

  private final String messageBody;

  public StaticMessageGenerator(String messageBody) {
    this.messageBody = messageBody;
  }

  @Override
  public String generateMessage() {
    return messageBody;
  }

}
