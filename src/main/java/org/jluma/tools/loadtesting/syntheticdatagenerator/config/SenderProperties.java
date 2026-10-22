package org.jluma.tools.loadtesting.syntheticdatagenerator.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;
import java.nio.file.Paths;

@Data
@ConfigurationProperties(prefix = "writer")
public class SenderProperties {

    private KafkaProps kafka = new KafkaProps();
    private FileProps file = new FileProps();

    @Data
    public static class FileProps {
        private Path path = Paths.get("./output.txt");
    }

    @Data
    public static class KafkaProps {
        private String topic = "default_topic";
    }

}
