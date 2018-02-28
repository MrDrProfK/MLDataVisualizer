package recitation.pkg5;

import java.util.Iterator;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

public class Recitation5 extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Line Chart Sample");

        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Number of Month");
        yAxis.setLabel("Stock Price");

        final LineChart lineChart = new LineChart(xAxis, yAxis);

        lineChart.setTitle("Stock Monitoring, 2010");
        lineChart.setCreateSymbols(false);

        XYChart.Series series = new XYChart.Series();
        series.setName("My portfolio");

        series.getData().add(new XYChart.Data(1, 23));
        series.getData().add(new XYChart.Data(2, 14));
        series.getData().add(new XYChart.Data(3, 15));
        series.getData().add(new XYChart.Data(4, 24));
        series.getData().add(new XYChart.Data(5, 34));
        series.getData().add(new XYChart.Data(6, 36));
        series.getData().add(new XYChart.Data(7, 22));
        series.getData().add(new XYChart.Data(8, 45));
        series.getData().add(new XYChart.Data(9, 43));
        series.getData().add(new XYChart.Data(10, 17));
        series.getData().add(new XYChart.Data(11, 29));
        series.getData().add(new XYChart.Data(12, 25));

        XYChart.Series series2 = new XYChart.Series();
        series2.setName("My portfolio 2");

        series2.getData().add(new XYChart.Data(1, 25));
        series2.getData().add(new XYChart.Data(2, 16));
        series2.getData().add(new XYChart.Data(3, 17));
        series2.getData().add(new XYChart.Data(4, 26));
        series2.getData().add(new XYChart.Data(5, 36));
        series2.getData().add(new XYChart.Data(6, 34));
        series2.getData().add(new XYChart.Data(7, 20));
        series2.getData().add(new XYChart.Data(8, 43));
        series2.getData().add(new XYChart.Data(9, 44));
        series2.getData().add(new XYChart.Data(10, 11));
        series2.getData().add(new XYChart.Data(11, 20));
        series2.getData().add(new XYChart.Data(12, 29));

        XYChart.Series series3 = new XYChart.Series();
        series3.setName("My portfolio 3");

        // create iterators for each data set (each series)
        Iterator seriesIterator = series.getData().iterator();
        Iterator series2Iterator = series2.getData().iterator();

        // while there is available chart data in both series...
        while (seriesIterator.hasNext() && series2Iterator.hasNext()) {
            // collect data object from series data
            XYChart.Data s1Data = ((XYChart.Data) (seriesIterator.next()));
            // extract x value from series data coordinate
            Integer s1xVal = new Integer((s1Data.getXValue().toString()));
            // extract y value from series data coordinate
            Integer s1yVal = new Integer((s1Data.getYValue().toString()));
            // extract y value from series2 data coordinate
            Integer s2yVal = new Integer((((XYChart.Data) (series2Iterator.next())).getYValue().toString()));
            // add corresponding y-values (from series and series2) to form series3
            series3.getData().add(new XYChart.Data(s1xVal, s1yVal + s2yVal));
        }
        Scene scene = new Scene(lineChart, 800, 600);
        // add data from series, series2, and series3 to the line chart
        lineChart.getData().addAll(series, series2, series3);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
