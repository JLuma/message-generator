package ru.sbt.tengri.kafka.message.generator;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Myan {

    private static final Logger LOG = LoggerFactory.getLogger(Myan.class);

    private final int threads;
    private final String topic;
    private final int messagesAmount;
    private final String bootstrapServers;

    private Myan() {
        this.threads = ConfigurationUtils.getThreadsNumberProperty();
        this.topic = ConfigurationUtils.getTargetTopicProperty();
        this.messagesAmount = ConfigurationUtils.getMessagesNumberProperty();
        this.bootstrapServers = ConfigurationUtils.getKafkaServersProperty();

        LOG.info("Starting with configuration:\n" +
                "Topic [{}]\n" +
                "Bootstrap servers [{}]\n" +
                "Messages amount [{}]\n" +
                "Threads [{}]\n", topic, bootstrapServers, messagesAmount, threads);
    }

    public static void main(String[] args) throws InterruptedException {
        new Myan().run();
    }

    private void run() throws InterruptedException {
        long startTime = System.currentTimeMillis();

        ExecutorService es = Executors.newFixedThreadPool(threads);
        CountDownLatch cd = new CountDownLatch(threads);
        final int messagesPerThread = calculateMessagesPerThread(messagesAmount, threads);
        try(KafkaProducer<String, String> producer = createKafkaProducer()) {
            for (int i = 0; i < threads; i++) {
                es.submit(new WriteMessagesTask(cd, producer, topic, messagesPerThread));
            }
            cd.await();
        }

        LOG.info("Execution time {} (ms)", (System.currentTimeMillis() - startTime));
        es.shutdown();
    }

    private int calculateMessagesPerThread(int messagesTotal, int threads) {
        int messagesPerThread = messagesTotal / threads;
        messagesPerThread = messagesPerThread > 0 ? messagesPerThread : 1;
        LOG.info("Every thread will send {} messages", messagesPerThread);
        return messagesPerThread;
    }

    private KafkaProducer<String, String> createKafkaProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaMessageGenerator");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        Properties jvmProperties = System.getProperties();
        props.putAll(jvmProperties);
        LOG.info("Creating kafka producer with JVM properties {}\n" +
                "Warning: this properties will OVERRIDE any producer configuration (including bootstrap servers)!",
                jvmProperties);
        return new KafkaProducer<>(props);
    }

}
