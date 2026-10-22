package org.jluma.tools.loadtesting.syntheticdatagenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.jluma.tools.loadtesting.syntheticdatagenerator.config.MessageGeneratorProperties;
import org.jluma.tools.loadtesting.syntheticdatagenerator.config.KafkaProducerProperties;

/**
 * Spring Boot application entry point for the message generator.
 */
@SpringBootApplication
@EnableConfigurationProperties({
    MessageGeneratorProperties.class,
    KafkaProducerProperties.class
})
public class MyanApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyanApplication.class, args);
    }
}
