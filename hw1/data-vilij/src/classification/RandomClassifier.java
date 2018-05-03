package classification;

import algorithms.AlgorithmPauser;
import algorithms.Classifier;
import data.DataSet;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.StackPane;

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
    
    private final AlgorithmPauser pauser;
    //defining a series
    private XYChart.Series series = new XYChart.Series();
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
                            boolean tocontinue, 
                            LineChart<Number, Number> chart,
                            AlgorithmPauser pauser) {
        this.dataset = dataset;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.tocontinue = new AtomicBoolean(tocontinue);
        this.chart = chart;
        this.pauser = pauser;
    }

    @Override
    public void run() {
        series.setName("Random Classifier");
        Platform.runLater(() -> {
            chart.setAnimated(false);
            chart.getData().add(series);
        });
//        for (int i = 1; i <= maxIterations && tocontinue(); i++) {
        for (int i = 1; i <= maxIterations; i++) {
            try {
                pauser.shouldIPause();
            } catch (InterruptedException ex) {
                Logger.getLogger(RandomClassifier.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            int xCoefficient = new Double(RAND.nextDouble() * 100).intValue();
            int yCoefficient = new Double(RAND.nextDouble() * 100).intValue();
            int constant = new Double(RAND.nextDouble() * 100).intValue();

            // this is the real output of the classifier
            output = Arrays.asList(xCoefficient, yCoefficient, constant);

            // everything below is just for internal viewing of how the output is changing
            // in the final project, such changes will be dynamically visible in the UI
            if (i % updateInterval == 0) {
                System.out.printf("Iteration number %d: ", i);
                flush();
            }
            if (i > maxIterations * .6 && RAND.nextDouble() < 0.05) {
                System.out.printf("Iteration number %d: ", i);
                flush();
                break;
            }
            try {
                Thread.sleep(300);
            } catch (InterruptedException ex) {
                Logger.getLogger(RandomClassifier.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Platform.runLater(() -> {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("ALERT");
                alert.setHeaderText("");
                alert.setContentText("Algorithm Run Complete!");
                alert.showAndWait();
            });
    }

    // for internal viewing only
    protected void flush() {
        System.out.printf("%d\t%d\t%d%n", output.get(0), output.get(1), output.get(2));

        double xMin = dataset.getBounds("xMin");
        double xMax = dataset.getBounds("xMax");
        double yMin = dataset.getBounds("yMin");
        double yMax = dataset.getBounds("yMax");

        double domainSize = xMax - xMin;
        if (domainSize < 1) {
            domainSize = 1;
        }

        double rangeSize = yMax - yMin;
        if (rangeSize < 1) {
            rangeSize = 1;
        }
        
        xMin -= (domainSize * .05);
        xMax += (domainSize * .05);
        yMin -= (rangeSize * .05);
        yMax += (rangeSize * .05);
        
        if (output.get(0) == 0 && output.get(1) == 0) {
            Platform.runLater(() -> {
//                Alert alert = new Alert(AlertType.WARNING);
//                alert.setTitle("ALERT");
//                alert.setContentText("Degenerate line produced by classification algorithm.");
//                alert.showAndWait();
            });
            return;
        }
        
        double pt1x, pt1y, pt2x, pt2y;

        if (output.get(0) == 0) {
            double y = -output.get(2) / output.get(1);
            if (y >= yMin && y <= yMax) {
                pt1x = xMin;
                pt2x = xMax;
                pt1y = pt2y = y;
                
                final double pt1xfinal = pt1x;
                final double pt1yfinal = pt1y;
                final double pt2xfinal = pt2x;
                final double pt2yfinal = pt2y;
                
                Platform.runLater(() -> {
                    series.getData().clear();
                    //populating the series with data
                    series.getData().addAll(new XYChart.Data(pt1xfinal, pt1yfinal), new XYChart.Data(pt2xfinal, pt2yfinal));
                    for (XYChart.Data<Number, Number> dataPt : ((XYChart.Series<Number, Number>) series).getData()) {
                        StackPane stackPane = (StackPane) dataPt.getNode();
                        stackPane.setVisible(false);
                    }
                });
            } else {
                Platform.runLater(() -> {
//                    Alert alert = new Alert(AlertType.WARNING);
//                    alert.setTitle("ALERT");
//                    alert.setContentText("Line Out of Range.");
//                    alert.showAndWait();
                });
                return;
            }
        }
        
        if (output.get(1) == 0) {
            double x = -output.get(2) / output.get(0);
            if (x >= xMin && x <= xMax) {
                pt1x = pt2x = x;
                pt1y = yMin;
                pt2y = yMax;
                
                final double pt1xfinal = pt1x;
                final double pt1yfinal = pt1y;
                final double pt2xfinal = pt2x;
                final double pt2yfinal = pt2y;
                
                Platform.runLater(() -> {
                    series.getData().clear();
                    //populating the series with data
                    series.getData().addAll(new XYChart.Data(pt1xfinal, pt1yfinal), new XYChart.Data(pt2xfinal, pt2yfinal));
                    for (XYChart.Data<Number, Number> dataPt : ((XYChart.Series<Number, Number>) series).getData()) {
                        StackPane stackPane = (StackPane) dataPt.getNode();
                        stackPane.setVisible(false);
                    }
                });
            } else {
                Platform.runLater(() -> {
//                    Alert alert = new Alert(AlertType.WARNING);
//                    alert.setTitle("ALERT");
//                    alert.setContentText("Line Out of Range.");
//                    alert.showAndWait();
                });
                return;
            }
        }
        
        // y(xMin)
        double tempY1 = ((-output.get(0) * xMin) - output.get(2)) / output.get(1);
        // y(xMax)
        double tempY2 = ((-output.get(0) * xMax) - output.get(2)) / output.get(1);
        
        boolean slopeIsPos = true;
        if (tempY1 > tempY2) {
            slopeIsPos = false;
        }
        
        if (slopeIsPos) {
            if (tempY1 >= yMin && tempY1 <= yMax) {
                pt1x = xMin;
                pt1y = tempY1;
            } else {
                double tempX1 = ((-output.get(1) * yMin) - output.get(2)) / output.get(0);
                if (tempX1 >= xMin && tempX1 <= xMax) {
                    pt1x = tempX1;
                    pt1y = yMin;
                } else {
                    Platform.runLater(() -> {
//                        Alert alert = new Alert(AlertType.WARNING);
//                        alert.setTitle("ALERT");
//                        alert.setContentText("Line Out of Range.");
//                        alert.showAndWait();
                    });
                    return;
                }
            }

            if (tempY2 >= yMin && tempY2 <= yMax) {
                pt2x = xMax;
                pt2y = tempY2;
            } else {
                double tempX2 = ((-output.get(1) * yMax) - output.get(2)) / output.get(0);
                pt2x = tempX2;
                pt2y = yMin;
            }
        } else {
            if (tempY1 >= yMin && tempY1 <= yMax) {
                pt1x = xMin;
                pt1y = tempY1;
            } else {
                double tempX1 = ((-output.get(1) * yMax) - output.get(2)) / output.get(0);
                if (tempX1 >= xMin && tempX1 <= xMax) {
                    pt1x = tempX1;
                    pt1y = yMax;
                } else {
                    Platform.runLater(() -> {
//                        Alert alert = new Alert(AlertType.WARNING);
//                        alert.setTitle("ALERT");
//                        alert.setContentText("Line Out of Range.");
//                        alert.showAndWait();
                    });
                    return;
                }
            }

            if (tempY2 >= yMin && tempY2 <= yMax) {
                pt2x = xMax;
                pt2y = tempY2;
            } else {
                double tempX2 = ((-output.get(1) * yMin) - output.get(2)) / output.get(0);
                pt2x = tempX2;
                pt2y = yMin;
            }
        }

//        System.out.println("pt1x: " + pt1x + " pt1y: " + pt1y + " pt2x: " + pt2x + " pt2y: " + pt2y);
        final double pt1xfinal = pt1x;
        final double pt1yfinal = pt1y;
        final double pt2xfinal = pt2x;
        final double pt2yfinal = pt2y;
        
        Platform.runLater(() -> {
            series.getData().clear();
            //populating the series with data
            series.getData().addAll(new XYChart.Data(pt1xfinal, pt1yfinal), new XYChart.Data(pt2xfinal, pt2yfinal));
            for (XYChart.Data<Number, Number> dataPt : ((XYChart.Series<Number, Number>) series).getData()) {
                StackPane stackPane = (StackPane) dataPt.getNode();
                stackPane.setVisible(false);
            }
        });
    }

    /** A placeholder main method to just make sure this code runs smoothly */
    public static void main(String... args) throws IOException {
        DataSet          dataset    = DataSet.fromTSDFile(Paths.get("/path/to/some-data.tsd"));
//        RandomClassifier classifier = new RandomClassifier(dataset, 100, 5, true, new LineChart());
//        classifier.run(); // no multithreading yet
    }
}