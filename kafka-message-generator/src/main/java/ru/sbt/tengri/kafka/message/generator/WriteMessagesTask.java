package ru.sbt.tengri.kafka.message.generator;

import java.net.URI;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import ru.sbt.tengri.json.message.generator.MessageGenerator;

public class WriteMessagesTask implements Runnable {

    private final CountDownLatch cd;

    private final URI messageSchemaFileUri;
    private final KafkaProducer<String, String> producer;
    private final String topic;
    private final long messagesAmount;

    public WriteMessagesTask(
        CountDownLatch cd,
        KafkaProducer<String, String> producer,
        String topic,
        long messagesAmount,
        URI messageSchemaFileUri) {

        this.cd = cd;
        this.producer = producer;
        this.topic = topic;
        this.messagesAmount = messagesAmount;
        this.messageSchemaFileUri = messageSchemaFileUri;
    }

    public void run() {
        final MessageGenerator gen = new MessageGenerator(messageSchemaFileUri);
        for (int i = 0; i < messagesAmount; i++) {
            producer.send(new ProducerRecord<>(topic, UUID.randomUUID().toString(), gen.generateMessage()));
        }
        cd.countDown();
    }

}
