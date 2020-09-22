package ru.sbt.tengri.kafka.message.generator.proxy;

import ru.sbt.tengri.json.message.generator.MessageGenerator;
import ru.sbt.tengri.kafka.message.generator.Configuration;

public class OriginalMessageGeneratorProxyFactory implements MessageGeneratorProxyFactory {

  private final MessageGenerator messageGenerator;

  public OriginalMessageGeneratorProxyFactory(Configuration conf) {
    this.messageGenerator = new MessageGenerator(conf.getMessageSchemaFileUri());
  }

  @Override
  public MessageGeneratorProxy createMessageGeneratorProxy() {
    return new MessageGeneratorProxyImpl(messageGenerator);
  }

}
