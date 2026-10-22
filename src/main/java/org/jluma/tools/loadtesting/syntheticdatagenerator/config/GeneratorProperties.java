package org.jluma.tools.loadtesting.syntheticdatagenerator.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;

@Data
@ConfigurationProperties(prefix = "generator")
public class GeneratorProperties {

    private MessageGenerationMode mode = MessageGenerationMode.SCHEMA_BASED;
    private ConstantFileConfig constantFile = new ConstantFileConfig();
    private SchemaBasedConfig schemaBased = new SchemaBasedConfig();

    @Data
    public static class ConstantFileConfig {
        private Path messageFilePath;
    }

    @Data
    public static class SchemaBasedConfig {
        private Path schemaFilePath;
    }

    public enum MessageGenerationMode {
        CONSTANT_FILE,
        SCHEMA_BASED
    }

}
