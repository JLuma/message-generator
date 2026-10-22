package org.jluma.tools.loadtesting.syntheticdatagenerator;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.jluma.tools.loadtesting.syntheticdatagenerator.api.MessageGenerationException;
import org.jluma.tools.loadtesting.syntheticdatagenerator.api.MessageGenerator;
import org.jluma.tools.loadtesting.syntheticdatagenerator.api.MessageGeneratorInitializationException;
import org.jluma.tools.loadtesting.syntheticdatagenerator.writer.api.MessageWriter;
import org.jluma.tools.loadtesting.syntheticdatagenerator.writer.kafka.KafkaMessageWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jluma.tools.loadtesting.syntheticdatagenerator.generator.constant_file.ConstantFileMessageGenerator;
import org.jluma.tools.loadtesting.syntheticdatagenerator.generator.schema_based.SchemaBasedMessageGenerator;

public class Myan {

    private static final Logger LOG = LoggerFactory.getLogger(Myan.class);
    private final Configuration conf;

    public Myan(Configuration conf) {
        this.conf = conf;
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = loadConfiguration();
        Myan myan = new Myan(conf);
        myan.run();
    }

    private static Configuration loadConfiguration() {
        String configFilePathAsString = System.getProperty("config.file");
        Properties props = new Properties();
        if (StringUtils.isNoneEmpty(configFilePathAsString)) {
            try (FileInputStream is = new FileInputStream(configFilePathAsString)) {
                props.load(is);
            } catch (IOException ex) {
                throw new RuntimeException(
                        String.format(
                                "Cannot read configuration file from path [%s]",
                                configFilePathAsString));
            }
        }
        props.putAll(System.getProperties());
        return new Configuration(props);
    }

    private void run() throws IOException, InterruptedException, MessageGenerationException, MessageGeneratorInitializationException {
        MessageGenerator messageGenerator = constructMessageGenerator();
        messageGenerator.initialize();
        if (conf.isSampleMode()) {
            LOG.info("Running in sample mode");
            writeSample(messageGenerator);
        } else {
            MessageWriter messageWriter = constructMessageWriter();
            if (messageWriter instanceof KafkaMessageWriter kafkaWriter) {
                kafkaWriter.addKafkaProducerAdditionalProperties(getKafkaProducerAdditionalProperties());
            }
            messageWriter.writeMessages(messageGenerator);
        }
    }

    private MessageGenerator constructMessageGenerator() {
        Configuration.MessageGeneratorType mode = conf.getMessageGenerationMode();
        switch (mode) {
            case SCHEMA_BASED:
                return new SchemaBasedMessageGenerator(conf.getMessageSchemaFileUri());
            case CONSTANT_FILE:
                return new ConstantFileMessageGenerator(conf.getConstantFileMessageUri());
            default:
                throw new IllegalArgumentException(
                        String.format("This method doesn't not support this message generation mode [%s]", mode));
        }
    }

    private MessageWriter constructMessageWriter() {
        Configuration.MessageTypeWriterType mode = conf.getMessageWriterType();
        switch (mode) {
            case KAFKA:
                return new KafkaMessageWriter(conf);
            default:
                throw new IllegalArgumentException(
                        String.format("This method doesn't not support this message writer type [%s]", mode));
        }
    }

    private void writeSample(MessageGenerator messageGenerator) throws IOException, MessageGenerationException {
        String sampleMessage = messageGenerator.generateMessage();
        try(FileOutputStream fos = new FileOutputStream("./sample.json")) {
            fos.write(sampleMessage.getBytes());
        }
        System.out.println(sampleMessage);
    }

    private Properties getKafkaProducerAdditionalProperties() {
        Properties props = conf.getConfAsProperties();
        Properties kafkaProducerAdditionalProperties = new Properties();
        final String kafkaPropertiesNamePrefix = "native.kafka.";
        for (Map.Entry<Object, Object> e : props.entrySet()) {
            String propertyName = (String) e.getKey();
            if (propertyName.startsWith(kafkaPropertiesNamePrefix)) {
                String targetPropertyName = propertyName.replace(kafkaPropertiesNamePrefix, "");
                kafkaProducerAdditionalProperties.put(targetPropertyName, e.getValue());
            }
        }
        return kafkaProducerAdditionalProperties;
    }

}
