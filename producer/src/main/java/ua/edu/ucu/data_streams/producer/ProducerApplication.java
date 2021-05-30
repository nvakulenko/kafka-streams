package ua.edu.ucu.data_streams.producer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.TopicExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProducerApplication {

  private static final Logger logger = LoggerFactory.getLogger(ProducerApplication.class);
  private static final String TOPIC = "reddit";
  public static final String REDDIT_DATA_SET = "train-balanced-sarcasm.csv";

  public static void main(String[] args) throws InterruptedException {

    SpringApplication.run(ProducerApplication.class, args);

    final Properties props = getProperties();

    // Create topic if needed
    createTopic(TOPIC, props);

    // wait until all consumers are up - to get more precise statistics
    Thread.sleep(8000);

    Producer<String, String> producer = new KafkaProducer<>(props);
    // Produce sample data
    InputStream dataFile = ProducerApplication.class.getClassLoader()
        .getResourceAsStream(REDDIT_DATA_SET);
    InputStreamReader inputStreamReader = new InputStreamReader(dataFile);

    int recordNbr = 0;
    Instant startedAt = Instant.now();
    try (BufferedReader br = new BufferedReader(inputStreamReader)) {
      String line = br.readLine(); // skip header

      while ((line = br.readLine()) != null || recordNbr < 100) {
        recordNbr++;
        line = Instant.now() + "," + line;
        producer.send(new ProducerRecord<>(TOPIC, Integer.toString(recordNbr), line),
            (m, e) -> {
              if (e != null) {
                e.printStackTrace();
              } else {
                logger.info("Produced record to topic " + m.topic() + " partition " + m.partition()
                    + " @ offset " + m.offset());
              }
            });
      }
    } catch (FileNotFoundException e) {
      logger.error(e.getLocalizedMessage(), e);
    } catch (IOException e) {
      logger.error(e.getLocalizedMessage(), e);
    }
    producer.flush();

    logger.info(
        recordNbr + " messages were produced to topic" + TOPIC + " and took " + Duration
            .between(startedAt, Instant.now()));

    producer.close();

    System.exit(0);
  }

  private static Properties getProperties() {
    final Properties props = new Properties();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
        System.getProperty("KAFKA_BOOTSTRAP_SERVERS",
            Optional.ofNullable(System.getenv("KAFKA_BOOTSTRAP_SERVERS"))
                .orElse("broker:9092")));
    props.put(ProducerConfig.ACKS_CONFIG, "all");
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
        "org.apache.kafka.common.serialization.StringSerializer");
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
        "org.apache.kafka.common.serialization.StringSerializer");
    return props;
  }

  // Create topic in Confluent Cloud
  public static void createTopic(final String topic, final Properties cloudConfig) {
    Integer partitions = Optional.ofNullable(System.getenv("KAFKA_PARTITIONS_COUNT"))
        .map(in -> Integer.valueOf(in)).orElse(1);
    logger.info("KAFKA_PARTITIONS_COUNT is " + partitions);

    final NewTopic newTopic = new NewTopic(topic, Optional.of(partitions), Optional.empty());
    try (final AdminClient adminClient = AdminClient.create(cloudConfig)) {
      adminClient.createTopics(Collections.singletonList(newTopic)).all().get();
    } catch (final InterruptedException | ExecutionException e) {
      // Ignore if TopicExistsException, which may be valid if topic exists
      if (!(e.getCause() instanceof TopicExistsException)) {
        throw new RuntimeException(e);
      }
    }
  }

}
