// Aaron Knoll
package clustering;

import algorithms.AlgorithmPauser;
import algorithms.Clusterer;
import data.DataSet;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import ui.AlgResourcePreparer;

/**
 * Modifies and displays DataSet with the number of clustering labels being 
 * passed in as a constructor parameter. The distribution of labels is random, 
 * without regard for data point locations or relationships (distance between 
 * data points, etc).
 * 
 * @author aaronknoll
 */
public class RandomClusterer extends Clusterer {

    private static final Random RAND = new Random();
    
    private final DataSet       dataset;

    private final int           maxIterations;
    private final int           updateInterval;
    // TRUE if in continuous run mode, otherwise FALSE
    // currently, this value does not change after instantiation
    private final AtomicBoolean tocontinue;
    
    private final AlgResourcePreparer   arp;
    private final AlgorithmPauser       pauser;
    
    private final LineChart<Number, Number> chart;

    
    public RandomClusterer(DataSet dataset,
                            int maxIterations,
                            int updateInterval,
                            int numberOfClusters,
                            boolean continuousRun,
                            AlgResourcePreparer arp) {
        
        super(numberOfClusters);
        this.dataset        = dataset;
        this.maxIterations  = maxIterations;
        this.updateInterval = updateInterval;
        this.tocontinue     = new AtomicBoolean(continuousRun);
        
        this.arp            = arp;
        this.chart          = arp.getChart();
        this.pauser         = arp.getPauser();
    }

    @Override
    public int getMaxIterations() { return maxIterations; }

    @Override
    public int getUpdateInterval() { return updateInterval; }

    @Override
    public boolean tocontinue() { return tocontinue.get(); }

    @Override
    public void run() {
        
        int iteration = 0;
        while (iteration++ < maxIterations) {
            try {
                pauser.shouldIPause();
            } catch (InterruptedException ex) {
                Logger.getLogger(KMeansClusterer.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            randomlyAssignLabels();
            if (iteration % updateInterval == 0) {
                System.out.println(dataset.getLabels());
                flush();
                if (!tocontinue()) {
                    pauser.pause();
                    Platform.runLater(() -> {
                        arp.alternateRunPause();
                    });
                }
            }
            if (iteration > maxIterations * .6 && RAND.nextDouble() < 0.05) {
//                flush();
//                System.out.println("An Early Break!");
                break;
            }
            if (tocontinue()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(RandomClusterer.class.getName()).log(Level.SEVERE, null, ex);
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

    private void randomlyAssignLabels() {
        dataset.getLabels().forEach((instanceName, label) -> {
            dataset.getLabels().put(instanceName, Integer.toString(RAND.nextInt(numberOfClusters)));
        });
    }
    
    /**
     * Flushes Algorithm output data to chart for plotting. 
     */
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
        return "Random Clustering";
    }
}