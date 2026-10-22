package org.jluma.tools.loadtesting.syntheticdatagenerator;

import lombok.extern.slf4j.Slf4j;
import org.jluma.tools.loadtesting.syntheticdatagenerator.api.MessageGenerator;
import org.jluma.tools.loadtesting.syntheticdatagenerator.api.MessageSender;

import java.util.concurrent.CountDownLatch;

@Slf4j
public class SendMessagesTask implements Runnable {

    private final CountDownLatch latch;
    private final long throttlingDelay;
    private final long messagesAmount;
    private final MessageSender messageSender;
    private final MessageGenerator messageGenerator;

    public SendMessagesTask(
        CountDownLatch latch,
        long throttlingDelay,
        long messagesAmount,
        MessageSender messageSender,
        MessageGenerator messageGenerator) {

        this.latch = latch;
        this.throttlingDelay = throttlingDelay;
        this.messagesAmount = messagesAmount;
        this.messageSender = messageSender;
        this.messageGenerator = messageGenerator;
    }

    @Override
    public void run() {
        for (int i = 0; i < messagesAmount; i++, addThrottling()) {
            try {
                messageSender.sendMessage(messageGenerator.generateMessage());
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
