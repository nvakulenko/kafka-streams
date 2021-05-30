package ua.edu.ucu.data_streams.consumer;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "metrics")
public class Statistic {

  @Id
  public String id;

  public String broker_message_key;
  public String consumer_id;
  public Instant producer_time;
  public Instant consumer_time;
  public Long consumer_time_in_seconds;
  public Integer record_length;
  public Long message_latency;

  public Statistic() {
  }

  public Statistic(String broker_message_key, String consumer_id, Instant producer_time,
      Instant consumer_time, Long consumer_time_in_seconds,
      Integer record_length, Long message_latency) {
    this.broker_message_key = broker_message_key;
    this.consumer_id = consumer_id;
    this.producer_time = producer_time;
    this.consumer_time = consumer_time;
    this.consumer_time_in_seconds = consumer_time_in_seconds;
    this.record_length = record_length;
    this.message_latency = message_latency;
  }
}
