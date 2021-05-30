package ua.edu.ucu.data_streams.reporter;

import java.awt.EventQueue;
import javax.swing.JFrame;

public class MainApplication extends JFrame {

  private static final MongoStatisticsCalculator mongoStatisticsCalculator = new MongoStatisticsCalculator();

  public static void main(final String[] args) {

    EventQueue.invokeLater(() -> {
      LatencyGraph ex = new LatencyGraph("Latency", mongoStatisticsCalculator);
      ex.setVisible(true);
    });

    EventQueue.invokeLater(() -> {
      ThroughputGraph ex = new ThroughputGraph("Throughput", mongoStatisticsCalculator);
      ex.setVisible(true);
    });

  }

}
