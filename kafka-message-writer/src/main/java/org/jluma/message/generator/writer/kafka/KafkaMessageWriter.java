package org.jluma.message.generator.writer.kafka;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.jluma.message.generator.api.MessageGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaMessageWriter {

  private static final Logger LOG = LoggerFactory.getLogger(KafkaMessageWriter.class);

  private final int threads;
  private final String topic;
  private final long messagesAmount;
  private final long throttlingDelay;
  private final String bootstrapServers;
  private final MessageGenerator messageGenerator;
  private final Properties kafkaProducerAdditionalProperties = new Properties();

  public KafkaMessageWriter(Configuration conf, MessageGenerator messageGenerator) {
    this.threads = conf.getThreadsNumberProperty();
    this.topic = conf.getTargetTopicProperty();
    this.messagesAmount = conf.getMessagesNumberProperty();
    this.bootstrapServers = conf.getKafkaServersProperty();
    this.throttlingDelay = conf.getThrottlingDelay();
    this.messageGenerator = messageGenerator;

    LOG.info("Starting with configuration:\n" +
                 "Topic [{}]\n" +
                 "Bootstrap servers [{}]\n" +
                 "Messages amount [{}]\n" +
                 "Throttling delay [{}]\n" +
                 "Threads [{}]\n", topic, bootstrapServers, messagesAmount, throttlingDelay, threads);
  }

  public void writeMessages() throws InterruptedException {
    long startTime = System.currentTimeMillis();

    ExecutorService es = Executors.newFixedThreadPool(threads);
    CountDownLatch cd = new CountDownLatch(threads);
    final long messagesPerThread = calculateMessagesPerThread(messagesAmount, threads);
    final long residualMessages = calculateResidualMessages(messagesAmount, threads);
    LOG.info(
        "Every thread will send {} messages. {} messages will be send by one of random threads",
        messagesPerThread,
        residualMessages);

    sendToKafka(es, cd, messagesPerThread, residualMessages, messageGenerator);

    LOG.info("Execution time {} (ms)", (System.currentTimeMillis() - startTime));
    es.shutdown();
  }

  public void addKafkaProducerAdditionalProperties(Properties properties) {
    this.kafkaProducerAdditionalProperties.putAll(properties);
  }

  private void sendToKafka(
      ExecutorService es,
      CountDownLatch cd,
      long messagesPerThread,
      long residualMessages,
      MessageGenerator messageGenerator) throws InterruptedException {

    try (KafkaProducer<String, String> producer = createKafkaProducer()) {
      for (int i = 0; i < threads - 1; i++) {
        es.submit(new WriteMessagesTask(cd, producer, topic, throttlingDelay, messagesPerThread, messageGenerator));
      }
      es.submit(new WriteMessagesTask(cd, producer, topic, throttlingDelay, messagesPerThread + residualMessages,
              messageGenerator));
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
    Properties producerProps = new Properties();
    producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    producerProps.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaMessageGenerator");
    producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    producerProps.putAll(kafkaProducerAdditionalProperties);

    LOG.info(
        "Creating kafka producer with additional properties {}\n" +
            "Warning: this properties will OVERRIDE any producer configuration (including bootstrap servers)!",
            kafkaProducerAdditionalProperties);
    return new KafkaProducer<>(producerProps);
  }

}
