package org.jluma.message.generator.writer.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import org.jluma.message.generator.api.MessageGenerationException;
import org.jluma.message.generator.api.MessageGenerator;

@Slf4j
public class WriteMessagesTask implements Runnable {

    private final CountDownLatch cd;

    private final KafkaProducer<String, String> producer;
    private final String topic;
    private final long throttlingDelay;
    private final long messagesAmount;
    private final MessageGenerator messageGenerator;

    public WriteMessagesTask(
        CountDownLatch cd,
        KafkaProducer<String, String> producer,
        String topic,
        long throttlingDelay,
        long messagesAmount,
        MessageGenerator messageGenerator) {

        this.cd = cd;
        this.producer = producer;
        this.topic = topic;
        this.throttlingDelay = throttlingDelay;
        this.messagesAmount = messagesAmount;
        this.messageGenerator = messageGenerator;
    }

    public void run() {
        for (int i = 0; i < messagesAmount; i++, addThrottling()) {
            try {
                producer.send(new ProducerRecord<>(topic, UUID.randomUUID().toString(), messageGenerator.generateMessage()));
            } catch (MessageGenerationException ex) {
                log.error("Cannot generate message", ex);
            }
        }
        cd.countDown();
    }

    private void addThrottling()  {
        try {
            if (throttlingDelay != 0) {
                Thread.sleep(throttlingDelay);
            }
        } catch (InterruptedException e) {
          throw new RuntimeException("Error while trying to add throttling");
        }
    }

}
