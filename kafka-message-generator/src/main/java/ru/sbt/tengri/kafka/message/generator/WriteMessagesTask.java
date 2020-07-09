package ru.sbt.tengri.kafka.message.generator;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.sbt.tengri.json.message.generator.TengriJsonGenerator;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;

public class WriteMessagesTask implements Runnable {

    private final CountDownLatch cd;

    private final KafkaProducer<String, String> producer;
    private final String topic;
    private final int messagesAmount;

    public WriteMessagesTask(CountDownLatch cd, KafkaProducer<String, String> producer, String topic, int messagesAmount) {
        this.cd = cd;
        this.producer = producer;
        this.topic = topic;
        this.messagesAmount = messagesAmount;
    }

    public void run() {
        final TengriJsonGenerator gen = new TengriJsonGenerator();
        for (int i = 0; i < messagesAmount; i++) {
            producer.send(new ProducerRecord<>(topic, UUID.randomUUID().toString(), gen.generateMessage()));
        }
        cd.countDown();
    }

}
