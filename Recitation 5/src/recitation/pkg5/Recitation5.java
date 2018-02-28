package recitation.pkg5;

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
        
        Scene scene = new Scene(lineChart, 800, 600);
        lineChart.getData().addAll(series, series2);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}