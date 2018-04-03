// Aaron Knoll
package dataprocessors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import javafx.geometry.Point2D;
import javafx.scene.chart.XYChart;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import javafx.scene.control.Tooltip;

/**
 * The data files used by this data visualization applications follow a tab-separated format, where each data point is
 * named, labeled, and has a specific location in the 2-dimensional X-Y plane. This class handles the parsing and
 * processing of such data. It also handles exporting the data to a 2-D plot.
 * <p>
 * A sample file in this format has been provided in the application's <code>resources/data</code> folder.
 *
 * @author Ritwik Banerjee
 * @see XYChart
 */
public final class TSDProcessor {

    public static class InvalidDataNameException extends Exception {

        private static final String NAME_ERROR_MSG = "All data instance names must start with the @ character.";

        public InvalidDataNameException(String name) {
            super(String.format("Invalid name '%s'. " + NAME_ERROR_MSG, name));
        }
    }

    public static class DuplicateInstanceNameException extends Exception {

        private static final String NAME_ERROR_MSG = "No duplicate instance names are permitted.";

        public DuplicateInstanceNameException(String name) {
            super(String.format("Duplicate instance name '%s' found. " + NAME_ERROR_MSG, name));
        }
    }

    private Map<String, String>  dataLabels;
    private Map<String, Point2D> dataPoints;

    public TSDProcessor() {
        dataLabels = new HashMap<>();
        dataPoints = new HashMap<>();
    }

    /**
     * Processes the data and populated two {@link Map} objects with the data.
     *
     * @param tsdString the input data provided as a single {@link String}
     * @throws Exception if the input string does not follow the <code>.tsd</code> data format
     */
    public void processString(String tsdString) throws Exception {
        AtomicBoolean hadAnError = new AtomicBoolean(false);
        StringBuilder errorMessage = new StringBuilder();
        Stream.of(tsdString.split("\n"))
                .map(line -> Arrays.asList(line.split("\t")))
                .forEach(list -> {
                    try {
                        String name = checkedname(list.get(0));
                        String label = list.get(1);
                        String[] pair = list.get(2).split(",");
                        Point2D point = new Point2D(Double.parseDouble(pair[0]), Double.parseDouble(pair[1]));
                        if (dataLabels.containsKey(name)) {
                            throw new DuplicateInstanceNameException(name);
                        }
                        dataLabels.put(name, label);
                        dataPoints.put(name, point);
                    } catch (Exception e) {
                        errorMessage.setLength(0);
                        errorMessage.append(e.getClass().getSimpleName()).append(": ").append(e.getMessage());
                        hadAnError.set(true);
                    }
                });
        if (errorMessage.length() > 0) {
            throw new Exception(errorMessage.toString());
        }
    }

    /**
     * Exports the data to the specified 2-D chart.
     *
     * @param chart the specified chart
     */
    void toChartData(XYChart<Number, Number> chart) {
        Set<String> labels = new HashSet<>(dataLabels.values());
        for (String label : labels) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(label);
            dataLabels.entrySet().stream().filter(entry -> entry.getValue().equals(label)).forEach(entry -> {
                Point2D point = dataPoints.get(entry.getKey());
                series.getData().add(new XYChart.Data<>(point.getX(), point.getY()));
            });
            chart.getData().add(series);
        }
        
    }
    
        /**
     * Exports the data to the specified 2-D chart.
     *
     * @param chart the specified chart
     */
    void displayInstanceNamesWhenHovering(XYChart<Number, Number> chart) {
        for (XYChart.Series<Number, Number> series : chart.getData()) {
            for (XYChart.Data<Number, Number> pts : series.getData()) {
                Iterator itr = dataPoints.entrySet().iterator();
                while (itr.hasNext()) {
                    Map.Entry pair = (Map.Entry) itr.next();
                    Tooltip.install(pts.getNode(), new Tooltip(pair.getKey().toString()));
                    if (((Point2D) pair.getValue()).getX() == pts.getXValue().doubleValue()
                            && ((Point2D) pair.getValue()).getY() == pts.getYValue().doubleValue()) {
                        Tooltip toolTip = new Tooltip(pair.getKey().toString());
//                        toolTip.getScene().setCursor(Cursor.WAIT);
                        pts.getNode().setOnMouseEntered(e->{
//                            toolTip.getScene().setCursor(Cursor.WAIT);
//                            chart.getScene().setCursor(Cursor.WAIT);
                            System.out.println("in node");
                            });
                        
                        Tooltip.install(pts.getNode(), toolTip);
                        break;
                    }
                }
                
            }
//            System.out.println(series);
        }
    }

    void clear() {
        dataPoints.clear();
        dataLabels.clear();
    }

    private String checkedname(String name) throws InvalidDataNameException {
        if (!name.startsWith("@"))
            throw new InvalidDataNameException(name);
        return name;
    }
    
    /**
     * Checks data for improper formatting/errors.
     * 
     * @param str   data to be analyzed
     * @return      the line number corresponding to data improperly formatted
     *              or -1 if no formatting errors are found.
     * @throws java.lang.Exception
     */
    public int getErrorLineNumber(String str) throws Exception {
        ArrayList<String> dataToBeCheckedForErrors = new ArrayList<>(Arrays.asList(str.split("\n")));
        ListIterator<String> itr = dataToBeCheckedForErrors.listIterator();

        int lineCounter = 1;
        try {
            while (itr.hasNext()) {
                processString(itr.next());
                lineCounter++;
            }
        } catch(Exception ex){
            if(ex.getMessage().contains("DuplicateInstanceNameException")){
                throw new Exception(ex.getMessage().split(": ", 2)[1]);
            }else{
                throw new Exception("Error on line #" + lineCounter + ".\nData must conform to the Tab-Separated Format. For example:\n@InstanceName[TAB Press]Label[TAB Press]X-Coord,Y-Coord");
            }
        }

        return -1;
    }
}
