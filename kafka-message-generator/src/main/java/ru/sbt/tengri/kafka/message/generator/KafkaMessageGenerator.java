package ru.sbt.tengri.kafka.message.generator;

import java.net.URI;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaMessageGenerator {

  private static final Logger LOG = LoggerFactory.getLogger(KafkaMessageGenerator.class);

  private final int threads;
  private final String topic;
  private final long messagesAmount;
  private final long throttlingDelay;
  private final String bootstrapServers;

  public KafkaMessageGenerator() {
    this.threads = ConfigurationUtils.getThreadsNumberProperty();
    this.topic = ConfigurationUtils.getTargetTopicProperty();
    this.messagesAmount = ConfigurationUtils.getMessagesNumberProperty();
    this.throttlingDelay = ConfigurationUtils.getThrottlingDelay();
    this.bootstrapServers = ConfigurationUtils.getKafkaServersProperty();

    LOG.info("Starting with configuration:\n" +
                 "Topic [{}]\n" +
                 "Bootstrap servers [{}]\n" +
                 "Messages amount [{}]\n" +
                 "Throttling delay [{}]\n" +
                 "Threads [{}]\n", topic, bootstrapServers, messagesAmount, throttlingDelay, threads);
  }

  public void generateMessages() throws InterruptedException {
    long startTime = System.currentTimeMillis();

    ExecutorService es = Executors.newFixedThreadPool(threads);
    CountDownLatch cd = new CountDownLatch(threads);
    final long messagesPerThread = calculateMessagesPerThread(messagesAmount, threads);
    final long residualMessages = calculateResidualMessages(messagesAmount, threads);
    LOG.info(
        "Every thread will send {} messages. {} messages will be send by one of random threads",
        messagesPerThread,
        residualMessages);

    final URI messageSchemaFileUri = ConfigurationUtils.getMessageSchemaFileUri();
    sendToKafka(es, cd, messagesPerThread, residualMessages, messageSchemaFileUri);

    LOG.info("Execution time {} (ms)", (System.currentTimeMillis() - startTime));
    es.shutdown();
  }

  private void sendToKafka(
      ExecutorService es,
      CountDownLatch cd,
      long messagesPerThread,
      long residualMessages,
      URI messageSchemaFileUri) throws InterruptedException {

    try (KafkaProducer<String, String> producer = createKafkaProducer()) {
      for (int i = 0; i < threads - 1; i++) {
        es.submit(new WriteMessagesTask(cd, producer, topic, throttlingDelay, messagesPerThread, messageSchemaFileUri));
      }
      es.submit(new WriteMessagesTask(cd, producer, topic, throttlingDelay, messagesPerThread + residualMessages, messageSchemaFileUri));
      cd.await();
    }
  }

  private long calculateMessagesPerThread(long messagesTotal, int threads) {
    return messagesTotal / threads;
  }

  private long calculateResidualMessages(long messagesTotal, int threads) {
    return messagesTotal % threads;
  }

  private KafkaProducer<String, String> createKafkaProducer() {
    Properties props = new Properties();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaMessageGenerator");
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

    Properties jvmProperties = System.getProperties();
    props.putAll(jvmProperties);
    LOG.info(
        "Creating kafka producer with JVM properties {}\n" +
            "Warning: this properties will OVERRIDE any producer configuration (including bootstrap servers)!",
        jvmProperties);
    return new KafkaProducer<>(props);
  }

}
