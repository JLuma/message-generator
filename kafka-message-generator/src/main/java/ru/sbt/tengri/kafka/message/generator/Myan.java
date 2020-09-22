package ru.sbt.tengri.kafka.message.generator;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.sbt.tengri.kafka.message.generator.proxy.MessageGeneratorProxy;
import ru.sbt.tengri.kafka.message.generator.proxy.MessageGeneratorProxyFactory;
import ru.sbt.tengri.kafka.message.generator.proxy.OriginalMessageGeneratorProxyFactory;
import ru.sbt.tengri.kafka.message.generator.proxy.StaticMessageGeneratorProxyFactory;

public class Myan {

    private static final Logger LOG = LoggerFactory.getLogger(Myan.class);
    private final Configuration conf;
    private final MessageGeneratorProxyFactory messageGeneratorProxyFactory;

    public Myan(Configuration conf) {
        this.conf = conf;
        this.messageGeneratorProxyFactory = createMessageGeneratorProxyFactory(conf);
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = loadConfiguration();
        Myan myan = new Myan(conf);
        myan.run();
    }

    private void run() throws IOException, InterruptedException {
        if (isSampleMode()) {
            LOG.info("Running in sample mode");
            writeSample();
        } else {
            new KafkaMessageGenerator(conf, messageGeneratorProxyFactory).generateMessages();
        }
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

    private boolean isSampleMode() {
        String isSampleMode = System.getProperty("sample-mode");
        return "true".equals(isSampleMode);
    }

    private void writeSample() throws IOException {
        MessageGeneratorProxy messageGeneratorProxy = messageGeneratorProxyFactory.createMessageGeneratorProxy();
        String sampleMessage = messageGeneratorProxy.generateMessage();
        try(FileOutputStream fos = new FileOutputStream("./sample.json")) {
            fos.write(sampleMessage.getBytes());
        }
        System.out.println(sampleMessage);
    }

    private MessageGeneratorProxyFactory createMessageGeneratorProxyFactory(Configuration conf) {
        Configuration.MessageGenerationMode mode = conf.getMessageGenerationMode();
        switch (mode) {
            case GENERATED:
                return new OriginalMessageGeneratorProxyFactory(conf);
            case STATIC:
                return new StaticMessageGeneratorProxyFactory(conf);
            default:
                throw new IllegalArgumentException(
                        String.format("This method doesn't not support this message generation mode [%s]", mode));
        }
    }

}
