package org.jluma.tools.loadtesting.syntheticdatagenerator.writer;

import org.jluma.tools.loadtesting.syntheticdatagenerator.config.KafkaProducerProperties;
import org.jluma.tools.loadtesting.syntheticdatagenerator.api.MessageGenerator;
import org.jluma.tools.loadtesting.syntheticdatagenerator.writer.kafka.KafkaWriterService;
import org.jluma.tools.loadtesting.syntheticdatagenerator.writer.kafka.WriteMessagesTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Service responsible for writing messages to Kafka using multiple threads.
 */
@Service
public class MessageWriterService {

    private static final Logger LOG = LoggerFactory.getLogger(MessageWriterService.class);

    private final KafkaProducerProperties kafkaProperties;
    private final KafkaWriterService kafkaWriterService;

    public MessageWriterService(
            KafkaProducerProperties kafkaProperties,
            KafkaWriterService kafkaWriterService) {
        this.kafkaProperties = kafkaProperties;
        this.kafkaWriterService = kafkaWriterService;
    }

    /**
     * Write messages to Kafka using multiple threads.
     */
    public void writeMessages(MessageGenerator messageGenerator) throws InterruptedException {
        int threads = kafkaProperties.getThreads();
        long messagesAmount = kafkaProperties.getMessagesAmount();
        long throttlingDelay = kafkaProperties.getSendThrottlingDelayMillis();

        long messagesPerThread = messagesAmount / threads;
        long residualMessages = messagesAmount % threads;

        LOG.info("Starting Kafka writer: topic={}, messages={}, threads={}, delay={}ms",
            kafkaProperties.getTopic(), messagesAmount, threads, throttlingDelay);
        LOG.info("Every thread will send {} messages. {} messages will be sent by one of random threads",
            messagesPerThread, residualMessages);

        long startTime = System.currentTimeMillis();

        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        try {
            // Create tasks for each thread
            for (int i = 0; i < threads - 1; i++) {
                executorService.submit(
                    new WriteMessagesTask(latch, kafkaWriterService, 
                        throttlingDelay, messagesPerThread, messageGenerator)
                );
            }
            // Last thread sends remaining messages
            executorService.submit(
                new WriteMessagesTask(latch, kafkaWriterService, 
                    throttlingDelay, messagesPerThread + residualMessages, messageGenerator)
            );

            latch.await();
        } finally {
            executorService.shutdown();
        }

        LOG.info("Execution time {} (ms)", (System.currentTimeMillis() - startTime));
    }
}
