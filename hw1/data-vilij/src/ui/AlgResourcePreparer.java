// Aaron Knoll
package ui;

import algorithms.AlgorithmPauser;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Label;

/**
 * Limits visibility of AppUI fields and methods.
 * @author aaronknoll
 */
public interface AlgResourcePreparer {
    
    void alternateRunPause();
    
    LineChart<Number, Number> getChart();
    
    AlgorithmPauser getPauser();
    
    Label getAlgNotificationLabel();
    
}
