package org.jluma.tools.loadtesting.syntheticdatagenerator.writer.kafka;

import lombok.extern.slf4j.Slf4j;
import org.jluma.tools.loadtesting.syntheticdatagenerator.api.MessageGenerationException;
import org.jluma.tools.loadtesting.syntheticdatagenerator.api.MessageGenerator;

import java.util.concurrent.CountDownLatch;

/**
 * Task for writing messages to Kafka in a separate thread.
 */
@Slf4j
public class WriteMessagesTask implements Runnable {

    private final CountDownLatch latch;
    private final KafkaWriterService kafkaWriterService;
    private final long throttlingDelay;
    private final long messagesAmount;
    private final MessageGenerator messageGenerator;

    public WriteMessagesTask(
        CountDownLatch latch,
        KafkaWriterService kafkaWriterService,
        long throttlingDelay,
        long messagesAmount,
        MessageGenerator messageGenerator) {

        this.latch = latch;
        this.kafkaWriterService = kafkaWriterService;
        this.throttlingDelay = throttlingDelay;
        this.messagesAmount = messagesAmount;
        this.messageGenerator = messageGenerator;
    }

    @Override
    public void run() {
        for (int i = 0; i < messagesAmount; i++, addThrottling()) {
            try {
                kafkaWriterService.writeMessage(messageGenerator);
            } catch (Exception ex) {
                log.error("Cannot write message", ex);
            }
        }
        latch.countDown();
    }

    private void addThrottling() {
        try {
            if (throttlingDelay != 0) {
                Thread.sleep(throttlingDelay);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error while trying to add throttling", e);
        }
    }
}
