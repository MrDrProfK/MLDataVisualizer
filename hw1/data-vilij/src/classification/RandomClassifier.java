package classification;

import algorithms.Classifier;
import data.DataSet;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Platform;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import ui.AppUI;

/**
 * @author Ritwik Banerjee
 */
public class RandomClassifier extends Classifier {

    private static final Random RAND = new Random();

    @SuppressWarnings("FieldCanBeLocal")
    // this mock classifier doesn't actually use the data, but a real classifier will
    private DataSet dataset;

    private final int maxIterations;
    private final int updateInterval;

    // currently, this value does not change after instantiation
    private final AtomicBoolean tocontinue;
    
    private final LineChart<Number, Number> chart;

    @Override
    public int getMaxIterations() {
        return maxIterations;
    }

    @Override
    public int getUpdateInterval() {
        return updateInterval;
    }

    @Override
    public boolean tocontinue() {
        return tocontinue.get();
    }

    public RandomClassifier(DataSet dataset,
                            int maxIterations,
                            int updateInterval,
                            boolean tocontinue, LineChart<Number, Number> chart) {
        this.dataset = dataset;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.tocontinue = new AtomicBoolean(tocontinue);
        this.chart = chart;
    }

    @Override
    public void run() {
        for (int i = 1; i <= maxIterations && tocontinue(); i++) {
            int xCoefficient = new Double(RAND.nextDouble() * 100).intValue();
            int yCoefficient = new Double(RAND.nextDouble() * 100).intValue();
            int constant     = new Double(RAND.nextDouble() * 100).intValue();

            // this is the real output of the classifier
            output = Arrays.asList(xCoefficient, yCoefficient, constant);

            // everything below is just for internal viewing of how the output is changing
            // in the final project, such changes will be dynamically visible in the UI
            if (i % updateInterval == 0) {


                System.out.printf("Iteration number %d: ", i); //
                flush();
            }
            if (i > maxIterations * .6 && RAND.nextDouble() < 0.05) {
                System.out.printf("Iteration number %d: ", i);
                flush();
                break;
            }
        }
    }

    // for internal viewing only
    protected void flush() {
        System.out.printf("%d\t%d\t%d%n", output.get(0), output.get(1), output.get(2));

        //defining a series
        XYChart.Series series = new XYChart.Series();
        series.setName("Random Classifier");
        //populating the series with data
        series.getData().add(new XYChart.Data(1, 5));
        series.getData().add(new XYChart.Data(2, 7));
        
        Platform.runLater(() -> {

//                double xMin = ((ValueAxis) chart.getXAxis()).getLowerBound();
//                double xMax = ((ValueAxis) chart.getXAxis()).getUpperBound();
//                
//                double yMin = ((ValueAxis) chart.getYAxis()).getLowerBound();
//                double yMax = ((ValueAxis) chart.getYAxis()).getUpperBound();
//                
//                double tempX, tempY;
//                tempY = ((-xCoefficient * xMin) - constant) / yCoefficient;
//                if (tempY > yMax) {
//
//                }
//                double pt1x, pt1y;
//                pt1y = 
//                double pt2x, pt2y;
                
//                series.getData().forEach(d -> {
//                    ((XYChart.Data)d).getNode().setVisible(false);
//                });
//                series.getNode().lookup(".chart-series-area-line").setStyle("-fx-stroke: rgba(" + rgb + ", 1.0);");;
//chart.getData().remove
chart.getData().add(series);
        });
    }

    /** A placeholder main method to just make sure this code runs smoothly */
    public static void main(String... args) throws IOException {
        DataSet          dataset    = DataSet.fromTSDFile(Paths.get("/path/to/some-data.tsd"));
//        RandomClassifier classifier = new RandomClassifier(dataset, 100, 5, true, new LineChart());
//        classifier.run(); // no multithreading yet
    }
}