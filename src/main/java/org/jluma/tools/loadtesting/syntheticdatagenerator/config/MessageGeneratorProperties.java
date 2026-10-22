package org.jluma.tools.loadtesting.syntheticdatagenerator.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;
import java.util.List;

/**
 * Properties for message generation configuration.
 */
@Data
@ConfigurationProperties(prefix = "message.generator")
public class MessageGeneratorProperties {

    /**
     * Enable sample mode - generates one example message and writes to file/console
     */
    private boolean sampleMode = false;

    /**
     * Message generation mode: SCHEMA_BASED or CONSTANT_FILE
     */
    private MessageGenerationMode mode = MessageGenerationMode.SCHEMA_BASED;

    /**
     * Configuration for CONSTANT_FILE mode
     */
    private ConstantFileConfig constantFile = new ConstantFileConfig();

    /**
     * Configuration for SCHEMA_BASED mode
     */
    private SchemaBasedConfig schemaBased = new SchemaBasedConfig();

    @Data
    public static class ConstantFileConfig {
        /**
         * Path to file containing constant/static message
         */
        private Path messageFilePath;
    }

    @Data
    public static class SchemaBasedConfig {
        /**
         * Path to JSON schema file for dynamic message generation
         */
        private Path schemaFilePath;
    }

    /**
     * Message generation mode enum
     */
    public enum MessageGenerationMode {
        CONSTANT_FILE,
        SCHEMA_BASED
    }
}
