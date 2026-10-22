package org.jluma.tools.loadtesting.syntheticdatagenerator.runner;

import lombok.RequiredArgsConstructor;
import org.jluma.tools.loadtesting.syntheticdatagenerator.api.Flow;
import org.jluma.tools.loadtesting.syntheticdatagenerator.api.MessageGenerator;
import org.jluma.tools.loadtesting.syntheticdatagenerator.api.MessageSender;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationRunnerImpl implements CommandLineRunner {

    private final MessageGenerator generator;
    private final MessageSender sender;

    private final Flow flow;

    @Override
    public void run(String... args) {
        flow.generateAndSendMessages(generator, sender);
    }

}
