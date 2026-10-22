package org.jluma.tools.loadtesting.syntheticdatagenerator.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "flow")
public class FlowProperties {

    private Long messagesAmount = 10L;
    private Integer threads = Runtime.getRuntime().availableProcessors();
    private Long throttlingDelayMillis = 0L;

}
