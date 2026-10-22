package org.jluma.tools.loadtesting.syntheticdatagenerator;

import lombok.RequiredArgsConstructor;
import org.jluma.tools.loadtesting.syntheticdatagenerator.api.Flow;
import org.jluma.tools.loadtesting.syntheticdatagenerator.api.MessageSender;
import org.jluma.tools.loadtesting.syntheticdatagenerator.config.FlowProperties;
import org.jluma.tools.loadtesting.syntheticdatagenerator.api.MessageGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@ConditionalOnProperty(name = "flow.thread-mode", havingValue = "multi")
@RequiredArgsConstructor
public class MultithreadFlow implements Flow {

    private static final Logger LOG = LoggerFactory.getLogger(MultithreadFlow.class);

    private final FlowProperties properties;

    public void generateAndSendMessages(MessageGenerator messageGenerator, MessageSender messageSender) {
        int threads = properties.getThreads();
        long messagesAmount = properties.getMessagesAmount();
        long throttlingDelay = properties.getThrottlingDelayMillis();

        if (!messageSender.isMultiThreadSupported()) {
            throw new IllegalStateException(
                    "MultiThread generation is not supported for [%s] sender".formatted(messageSender.getClass().getSimpleName()));
        }

        long messagesPerThread = messagesAmount / threads;
        long residualMessages = messagesAmount % threads;

        LOG.info("Starting message writer: messages={}, threads={}, sendThrottlingDelayMillis={}",
            messagesAmount, threads, throttlingDelay);
        LOG.info("Every thread will send {} messages. {} messages will be sent by one of random threads",
            messagesPerThread, residualMessages);

        long startTime = System.currentTimeMillis();

        try(ExecutorService executorService = Executors.newFixedThreadPool(threads);
            MessageSender sender = messageSender) {
            sender.initialize();
            CountDownLatch latch = new CountDownLatch(threads);
            for (int i = 0; i < threads - 1; i++) {
                executorService.submit(
                    new SendMessagesTask(latch, throttlingDelay, messagesPerThread, messageSender, messageGenerator)
                );
            }
            executorService.submit(
                new SendMessagesTask(latch, throttlingDelay, messagesPerThread + residualMessages, messageSender, messageGenerator)
            );
            awaitLatch(latch);
        }

        LOG.info("Execution time {} (ms)", (System.currentTimeMillis() - startTime));
    }

    private void awaitLatch(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

}
