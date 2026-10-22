package org.jluma.tools.loadtesting.syntheticdatagenerator.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "sender")
public class SenderProperties {

    private KafkaProps kafka = new KafkaProps();
    private FileProps file = new FileProps();
    private HttpProps http = new HttpProps();

    @Data
    public static class FileProps {
        private Path path = Paths.get("./output.txt");
    }

    @Data
    public static class KafkaProps {
        private String topic = "default_topic";
    }

    @Data
    public static class HttpProps {
        private String url = "http://localhost:8080/messages";
        private int connectionTimeoutMillis = 5000;
        private int socketTimeoutMillis = 10000;
        private int timeToLiveMillis = 60000;
        private int maxTotalConnections = 50;
        private int defaultMaxPerRoute = 25;
        private boolean keepAlive = true;
        private String method = "POST";
        private Map<String, String> headers = new HashMap<>();
    }

}
