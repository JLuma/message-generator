package org.jluma.tools.loadtesting.syntheticdatagenerator;

import lombok.RequiredArgsConstructor;
import org.jluma.tools.loadtesting.syntheticdatagenerator.api.Flow;
import org.jluma.tools.loadtesting.syntheticdatagenerator.api.MessageGenerator;
import org.jluma.tools.loadtesting.syntheticdatagenerator.api.MessageSender;
import org.jluma.tools.loadtesting.syntheticdatagenerator.config.FlowProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "flow.thread-mode", havingValue = "single", matchIfMissing = true)
@RequiredArgsConstructor
public class SingleThreadFlow implements Flow {

    private final FlowProperties properties;

    @Override
    public void generateAndSendMessages(MessageGenerator generator, MessageSender sender) {
        final long messageAmount = properties.getMessagesAmount();
        final long throttlingTimeMs = properties.getThrottlingDelayMillis();
        try (MessageSender s = sender) {
            s.initialize();
            for (int i = 0; i < messageAmount; i++, addThrottling(throttlingTimeMs)) {
                s.sendMessage(generator.generateMessage());
            }
        }
    }

    private void addThrottling(long ms) {
        if (ms == 0) {
            return;
        }
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

}
