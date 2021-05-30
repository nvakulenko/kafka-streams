package ua.edu.ucu.data_streams.consumer;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ConsumerApplication implements CommandLineRunner {

  private static final Logger logger = LoggerFactory.getLogger(ConsumerApplication.class);

  @Autowired
  private StatisticRepository repository;
  public static final String TOPIC = "reddit";

  public static void main(final String[] args) {
    SpringApplication.run(ConsumerApplication.class, args);
  }

  @Override
  public void run(String... args) throws InterruptedException {

    Thread.sleep(5000);

    final Properties props = new Properties();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
        System.getProperty("KAFKA_BOOTSTRAP_SERVERS",
            Optional.ofNullable(System.getenv("KAFKA_BOOTSTRAP_SERVERS"))
                .orElse("broker:9092")));
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
        "org.apache.kafka.common.serialization.StringDeserializer");
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
        "org.apache.kafka.common.serialization.StringDeserializer");
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "demo-consumer-1");

    final Consumer<String, String> consumer = new KafkaConsumer<String, String>(props);

    consumer.subscribe(Arrays.asList(TOPIC));

    Long total_count = 0L;
    String consumer_id = UUID.randomUUID().toString();

    try {
      while (true) {
        ConsumerRecords<String, String> records = consumer.poll(100);
        for (ConsumerRecord<String, String> record : records) {
          //Thread.sleep(1000);

          String key = record.key();
          String value = record.value();

          Instant producer_time = Instant.parse(value.substring(0, value.indexOf(",")));
          Instant consumer_time = Instant.now();
          Integer data_length = record.serializedValueSize();
          long message_latency = ChronoUnit.MILLIS.between(producer_time, consumer_time);
          long consumer_time_in_seconds = consumer_time.getEpochSecond();

          Statistic statistic = new Statistic(key, consumer_id, producer_time, consumer_time,
              consumer_time_in_seconds,
              data_length, message_latency);
          Statistic saved = repository.save(statistic);
          logger.info("Saved id: " + saved.id);

          total_count++;
          logger.info("Consumed record with key " + key + " and value " + value
              + ", and updated total count to " + total_count);
        }
      }
    } finally {
      consumer.close();
    }
  }

}
