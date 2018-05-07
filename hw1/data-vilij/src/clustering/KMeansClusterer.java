package clustering;

import algorithms.AlgorithmPauser;
import algorithms.Clusterer;
import data.DataSet;
import javafx.geometry.Point2D;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.application.Platform;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import ui.AlgResourcePreparer;

/**
 * @author Ritwik Banerjee
 */
public class KMeansClusterer extends Clusterer {

    private final DataSet       dataset;
    private List<Point2D>       centroids;

    private final int           maxIterations;
    private final int           updateInterval;
    private final AtomicBoolean tocontinue;
    
    private final AlgResourcePreparer   arp;
    private final AlgorithmPauser       pauser;
    
    private final LineChart<Number, Number> chart;

    // TRUE if in continuous run mode, otherwise FALSE
    // currently, this value does not change after instantiation
    private final boolean continuousRun;
    
    public KMeansClusterer(DataSet dataset,
                            int maxIterations,
                            int updateInterval,
                            int numberOfClusters,
                            boolean continuousRun,
                            AlgResourcePreparer arp) {
        
        super(numberOfClusters);
        this.dataset        = dataset;
        this.maxIterations  = maxIterations;
        this.updateInterval = updateInterval;
        this.tocontinue     = new AtomicBoolean(false);
        
        this.arp            = arp;
        this.chart          = arp.getChart();
        this.pauser         = arp.getPauser();
        this.continuousRun  = continuousRun;
    }

    @Override
    public int getMaxIterations() { return maxIterations; }

    @Override
    public int getUpdateInterval() { return updateInterval; }

    @Override
    public boolean tocontinue() { return tocontinue.get(); }

    @Override
    public void run() {
        
        initializeCentroids();
        int iteration = 0;
        while (iteration++ < maxIterations & tocontinue.get()) {
            if (pauser.terminateIfExitBtnClicked()) {
                return;
            }
            try {
                pauser.shouldIPause();
            } catch (InterruptedException ex) {
                return;
            }
            
            assignLabels();
            recomputeCentroids();
            if (iteration % updateInterval == 0) {
                
                flush();
                if (!continuousRun) {
                    pauser.pause();
                    Platform.runLater(() -> {
                        arp.alternateRunPause();
                    });
                }
            }
            
            if (continuousRun) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    return;
                }
            }
        }
        
        Platform.runLater(() -> {
            if (!pauser.isPaused()) {
                arp.alternateRunPause();
            }
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ALERT");
            alert.setHeaderText("");
            alert.setContentText("Algorithm Run Complete!");
            alert.showAndWait();
        });
    }

    private void initializeCentroids() {
        Set<String>  chosen        = new HashSet<>();
        List<String> instanceNames = new ArrayList<>(dataset.getLabels().keySet());
        Random       r             = new Random();
        while (chosen.size() < numberOfClusters) {
            int i = r.nextInt(instanceNames.size());
            while (instanceNames.size() < i && chosen.contains(instanceNames.get(i))) {
                ++i;
            }
            chosen.add(instanceNames.get(i));
        }
        centroids = chosen.stream().map(name -> dataset.getLocations().get(name)).collect(Collectors.toList());
        tocontinue.set(true);
    }

    private void assignLabels() {
        dataset.getLocations().forEach((instanceName, location) -> {
            double minDistance      = Double.MAX_VALUE;
            int    minDistanceIndex = -1;
            for (int i = 0; i < centroids.size(); i++) {
                double distance = computeDistance(centroids.get(i), location);
                if (distance < minDistance) {
                    minDistance = distance;
                    minDistanceIndex = i;
                }
            }
            dataset.getLabels().put(instanceName, Integer.toString(minDistanceIndex));
        });
    }

    private void recomputeCentroids() {
        tocontinue.set(false);
        IntStream.range(0, numberOfClusters).forEach(i -> {
            AtomicInteger clusterSize = new AtomicInteger();
            Point2D sum = dataset.getLabels()
                                 .entrySet()
                                 .stream()
                                 .filter(entry -> i == Integer.parseInt(entry.getValue()))
                                 .map(entry -> dataset.getLocations().get(entry.getKey()))
                                 .reduce(new Point2D(0, 0), (p, q) -> {
                                     clusterSize.incrementAndGet();
                                     return new Point2D(p.getX() + q.getX(), p.getY() + q.getY());
                                 });
            Point2D newCentroid = new Point2D(sum.getX() / clusterSize.get(), sum.getY() / clusterSize.get());
            if (!newCentroid.equals(centroids.get(i))) {
                centroids.set(i, newCentroid);
                tocontinue.set(true);
            }
        });
    }

    private static double computeDistance(Point2D p, Point2D q) {
        return Math.sqrt(Math.pow(p.getX() - q.getX(), 2) + Math.pow(p.getY() - q.getY(), 2));
    }
    
    private void flush() {
        Platform.runLater(() -> {
            chart.getData().clear();
            Set<String> labels = new HashSet<>(dataset.getLabels().values());
            for (String label : labels) {
                XYChart.Series<Number, Number> series = new XYChart.Series<>();
                series.setName(label);
                dataset.getLabels().entrySet().stream().filter(entry -> entry.getValue().equals(label)).forEach(entry -> {
                    Point2D point = dataset.getLocations().get(entry.getKey());
                    series.getData().add(new XYChart.Data<>(point.getX(), point.getY()));
                });
                chart.getData().add(series);
                series.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: transparent");
            }
        });
    }
    
    public static String getPrettyName(){
        return "K-Means Clustering";
    }
}