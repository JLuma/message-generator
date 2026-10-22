package org.jluma.tools.loadtesting.syntheticdatagenerator.generator;

import org.jluma.tools.loadtesting.syntheticdatagenerator.generator.constant_file.ConstantFileMessageGenerator;
import org.jluma.tools.loadtesting.syntheticdatagenerator.generator.schema_based.SchemaBasedMessageGenerator;
import org.jluma.tools.loadtesting.syntheticdatagenerator.api.MessageGenerator;

import java.nio.file.Path;
import java.net.URI;

/**
 * Factory for creating MessageGenerator implementations.
 */
public class MessageGeneratorFactory {

    public MessageGenerator createSchemaBasedGenerator(Path schemaPath) {
        return new SchemaBasedMessageGenerator(schemaPath);
    }

    public MessageGenerator createConstantFileGenerator(Path messagePath) {
        URI messageUri = messagePath != null ? messagePath.toUri() : null;
        return new ConstantFileMessageGenerator(messageUri);
    }
}
