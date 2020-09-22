package ru.sbt.tengri.kafka.message.generator;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import ru.sbt.tengri.kafka.message.generator.proxy.MessageGeneratorProxy;
import ru.sbt.tengri.kafka.message.generator.proxy.MessageGeneratorProxyFactory;

public class WriteMessagesTask implements Runnable {

    private final CountDownLatch cd;

    private final KafkaProducer<String, String> producer;
    private final String topic;
    private final long messagesAmount;
    private final MessageGeneratorProxyFactory messageGeneratorProxyFactory;

    public WriteMessagesTask(
        CountDownLatch cd,
        KafkaProducer<String, String> producer,
        String topic,
        long messagesAmount,
        MessageGeneratorProxyFactory messageGeneratorProxyFactory) {

        this.cd = cd;
        this.producer = producer;
        this.topic = topic;
        this.messagesAmount = messagesAmount;
        this.messageGeneratorProxyFactory = messageGeneratorProxyFactory;
    }

    public void run() {
        final MessageGeneratorProxy gen = messageGeneratorProxyFactory.createMessageGeneratorProxy();
        for (int i = 0; i < messagesAmount; i++) {
            producer.send(new ProducerRecord<>(topic, UUID.randomUUID().toString(), gen.generateMessage()));
        }
        cd.countDown();
    }

}
