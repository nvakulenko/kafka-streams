package ua.edu.ucu.data_streams.reporter;

import javax.swing.JFrame;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;

public class LatencyGraph extends JFrame {

  public LatencyGraph(final String title, MongoStatisticsCalculator mongoStatisticsCalculator)  {

    super(title);

    // create subplot latency...
    final XYDataset data2 = mongoStatisticsCalculator.getMessageLatency();

    final XYItemRenderer renderer2 = new StandardXYItemRenderer();
    final NumberAxis rangeAxis2 = new NumberAxis("Latency");
    rangeAxis2.setAutoRangeIncludesZero(false);
    final XYPlot subplot2 = new XYPlot(data2, null, rangeAxis2, renderer2);
    subplot2.setRangeAxisLocation(AxisLocation.TOP_OR_LEFT);

    final CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new NumberAxis("Message Nmbr"));
    plot.setGap(10.0);
    plot.add(subplot2, 1);
    plot.mapDatasetToRangeAxis(1, 1);
    plot.setOrientation(PlotOrientation.VERTICAL);

    // return a new chart containing the overlaid plot...
    final JFreeChart chart =  new JFreeChart("Latency",
        JFreeChart.DEFAULT_TITLE_FONT, plot, true);

    final ChartPanel panel = new ChartPanel(chart, true, true, true, true, true);
    panel.setPreferredSize(new java.awt.Dimension(1600, 700));
    setContentPane(panel);
  }

}
