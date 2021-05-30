package ua.edu.ucu.data_streams.reporter;

import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.sort;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Sorts;
import java.util.Arrays;
import org.bson.Document;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class MongoStatisticsCalculator {

  private MongoCollection<Document> metrics;

  public MongoStatisticsCalculator() {
    MongoClient mongoClient = new MongoClient("localhost", 27017);
    MongoDatabase database = mongoClient.getDatabase("statistics");
    metrics = database.getCollection("metrics");
  }

  // return X / Y : broker_message_key / message_latency
  public XYSeriesCollection getMessageLatency() {
    MongoCursor<Document> iterator = metrics.find().iterator();

    XYSeries series = new XYSeries("Message latency");
    while (iterator.hasNext()) {
      Document next = iterator.next();
      Double message_latency = Double.valueOf((next.getLong("message_latency")) / 1000);
      Integer broker_message_key = Integer.valueOf(next.getString("broker_message_key"));
      series.add(broker_message_key, message_latency);
    }

    XYSeriesCollection dataset = new XYSeriesCollection();
    dataset.addSeries(series);
    return dataset;
  }

  // Throughput by seconds - shows how many Mbts were processed by
  // the consumer group in each particular second of the execution
  public XYSeriesCollection getThroughput() {
    MongoCursor<Document> iterator = metrics.aggregate(Arrays.asList(
        group("$consumer_time_in_seconds", Accumulators.sum("throughput", "$record_length")),
        sort(Sorts.ascending("consumer_time_in_seconds")))).iterator();

    XYSeries series = new XYSeries("Mbps");

    int seconds = 0;
    while (iterator.hasNext()) {
      Document next = iterator.next();
      Double mbps =
          Double.valueOf((next.getInteger("throughput")) / 1024) / 1024; // bytes in megabytes
      series.add(seconds, mbps);
      seconds++;
    }

    XYSeriesCollection dataset = new XYSeriesCollection();
    dataset.addSeries(series);
    return dataset;
  }

}
