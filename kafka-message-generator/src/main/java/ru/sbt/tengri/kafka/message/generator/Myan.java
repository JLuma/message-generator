package ru.sbt.tengri.kafka.message.generator;

import java.io.FileOutputStream;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.sbt.tengri.json.message.generator.MessageGenerator;

public class Myan {

    private static final Logger LOG = LoggerFactory.getLogger(Myan.class);

    public static void main(String[] args) throws Exception {
        if (isSampleMode()) {
            LOG.info("Running in sample mode");
            new Myan().writeSample();
        } else {
            new KafkaMessageGenerator().generateMessages();
        }
    }

    private static boolean isSampleMode() {
        String isSampleMode = System.getProperty("sample-mode");
        return "true".equals(isSampleMode);
    }

    private void writeSample() throws IOException {
        MessageGenerator mg = new MessageGenerator(ConfigurationUtils.getMessageSchemaFileUri());
        String sampleMessage = mg.generateMessage();
        try(FileOutputStream fos = new FileOutputStream("./sample.json")) {
            fos.write(sampleMessage.getBytes());
        }
        System.out.println(sampleMessage);
    }

}
