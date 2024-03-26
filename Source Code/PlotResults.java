import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.chart.ui.UIUtils;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.ChartPanel;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class PlotResults extends JFrame {

    private final ArrayList<AlgorithmMetric> metrics;
    Map<String, double[]> stringArrayMap = new HashMap<>();

    private double getValue(String metricName,AlgorithmMetric metric) {
    	double value=0.0;
    	switch (metricName) {
	        case "ARUR":
	            value = metric.ARUR;
	            break;
	        case "Makespan":
	            value = metric.makespan;
	            break;
	        case "AvgMakespan":
	            value = metric.averageMakespan;
	            break;
	        case "Throughput":
	            value = metric.throughput;
	            break;
	        case "LoadImbalance":
	            value = metric.LoadImbalance;
	            break;
	    }
    	return value;
    	
    }
    
    public PlotResults(ArrayList<AlgorithmMetric> metrics) {
        super("Algorithm Metrics Comparison");

        this.metrics = metrics;

        JTabbedPane chartPanel = createChartPanel();
        chartPanel.setPreferredSize(new Dimension(800, 600));
        setContentPane(chartPanel);

        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private JTabbedPane createChartPanel() {
        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("ARUR", createChartPanel("ARUR"));
        tabbedPane.addTab("Makespan", createChartPanel("Makespan"));
        tabbedPane.addTab("Avg Makespan", createChartPanel("AvgMakespan"));
        tabbedPane.addTab("Throughput", createChartPanel("Throughput"));
        tabbedPane.addTab("LoadImbalnce", createChartPanel("LoadImbalance"));

        return tabbedPane;
    }

    private JPanel createChartPanel(String metricName) {
        JFreeChart chart = createChart(metricName);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setDomainZoomable(true);
        chartPanel.setRangeZoomable(true);
        chartPanel.setMouseWheelEnabled(true);
        return chartPanel;
    }

    private JFreeChart createChart(String metricName) {
        CategoryDataset dataset = createDataset(metricName);

        JFreeChart chart = ChartFactory.createBarChart(
                metricName + " Comparison",
                "Algorithm",
                metricName,
                dataset
        );

        CategoryPlot plot = chart.getCategoryPlot();
        CategoryAxis xAxis = plot.getDomainAxis();
        xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        double[] minMaxValue=stringArrayMap.get(metricName);
        if(minMaxValue[1]>3) {
        	yAxis.setRange(Math.max(minMaxValue[0]-1,0),minMaxValue[1]+1);
        }
        
        return chart;
    }

    private CategoryDataset createDataset(String metricName) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        double[] minMaxValue= {Double.MAX_VALUE,Double.MIN_VALUE};
        for (AlgorithmMetric metric : metrics) {
            double value = getValue(metricName,metric);
            minMaxValue[0]=Math.min(value, minMaxValue[0]);
            minMaxValue[1]=Math.max(value, minMaxValue[1]);
            dataset.addValue(value, metric.algorithmName, metric.algorithmName);
        }
        stringArrayMap.put(metricName, minMaxValue);
        return dataset;
    }

    public static void plotMetrics(ArrayList<AlgorithmMetric> metric) {
        SwingUtilities.invokeLater(() -> new PlotResults(metric).setVisible(true));
    }

    public static void main(String[] args) {
        // Example usage:
        ArrayList<AlgorithmMetric> metrics = new ArrayList<>();
//        metrics.add(new AlgorithmMetric("Algorithm1", 1.0, 2.0, 3.0, 4.0, 3.0));
//        metrics.add(new AlgorithmMetric("Algorithm2", 2.0, 3.0, 4.0, 5.0, 2.9));
//        metrics.add(new AlgorithmMetric("Algorithm3", 3.0, 4.0, 5.0, 6.0, 9.8));

        SwingUtilities.invokeLater(() -> new PlotResults(metrics).setVisible(true));
    }
}
