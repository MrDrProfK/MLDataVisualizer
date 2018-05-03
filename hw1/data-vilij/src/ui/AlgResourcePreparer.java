// Aaron Knoll
package ui;

import algorithms.AlgorithmPauser;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 *
 * @author aaronknoll
 */
public interface AlgResourcePreparer {
    
    LineChart<Number, Number> getChart();
    
    AlgorithmPauser getPauser();

    Button getRunPauseBtn();
    
    Label getAlgNotificationLabel();
    
}
