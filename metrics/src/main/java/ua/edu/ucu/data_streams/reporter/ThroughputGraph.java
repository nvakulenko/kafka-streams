package ua.edu.ucu.data_streams.reporter;

import java.awt.Font;
import javax.swing.JFrame;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;

public class ThroughputGraph extends JFrame {

  public ThroughputGraph(final String title, MongoStatisticsCalculator mongoStatisticsCalculator) {

    // create subplot 1...
    final XYDataset data1 = mongoStatisticsCalculator.getThroughput();

    final XYItemRenderer renderer1 = new StandardXYItemRenderer();
    final NumberAxis rangeAxis1 = new NumberAxis("Mbps");
    final XYPlot subplot1 = new XYPlot(data1, null, rangeAxis1, renderer1);
    subplot1.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);

    // parent plot...
    final CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new NumberAxis("Seconds"));
    plot.setGap(10.0);
    // add the subplots...
    plot.add(subplot1, 1);
    plot.mapDatasetToRangeAxis(0, 0);

    final XYTextAnnotation annotation = new XYTextAnnotation("Hello!", 50.0, 10000.0);
    annotation.setFont(new Font("SansSerif", Font.PLAIN, 9));
    annotation.setRotationAngle(Math.PI / 4.0);
    subplot1.addAnnotation(annotation);
    // parent plot...

    // return a new chart containing the overlaid plot...
    final JFreeChart chart =  new JFreeChart("Throughput",
        JFreeChart.DEFAULT_TITLE_FONT, plot, true);

    final ChartPanel panel = new ChartPanel(chart, true, true, true, false, true);
    panel.setPreferredSize(new java.awt.Dimension(500, 270));
    setContentPane(panel);
  }

}
