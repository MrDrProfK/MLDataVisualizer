package ui;

import actions.AppActions;
import dataprocessors.TSDProcessor;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import vilij.templates.ApplicationTemplate;
import vilij.templates.UITemplate;

/**
 * This is the application's user interface implementation.
 *
 * @author Ritwik Banerjee
 */
public final class AppUI extends UITemplate {

    /** The application to which this class of actions belongs. */
    ApplicationTemplate applicationTemplate;

    @SuppressWarnings("FieldCanBeLocal")
    private Button                       scrnshotButton; // toolbar button to take a screenshot of the data
    private ScatterChart<Number, Number> chart;          // the chart where data will be displayed
    private Button                       displayButton;  // workspace button to display data on the chart
    private TextArea                     textArea;       // text area for new data input
    private boolean                      hasNewText;     // whether or not the text area has any new data since last display

    private String                       scrnshoticonPath;// relative (partial) path to SCREENSHOT_ICON
    private TSDProcessor                 dataProcessor;  // for chart data manipulation
    
    public ScatterChart<Number, Number> getChart() { return chart; }

    public AppUI(Stage primaryStage, ApplicationTemplate applicationTemplate) {
        super(primaryStage, applicationTemplate);
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    protected void setResourcePaths(ApplicationTemplate applicationTemplate) {
        super.setResourcePaths(applicationTemplate);
        //set rel resource path for SCREENSHOT_ICON
        scrnshoticonPath = "/gui/icons/screenshot.png";
    }

    @Override
    protected void setToolBar(ApplicationTemplate applicationTemplate) {
        // TODO for homework 1
        // utilize super class method call for all but the final toolBarButton
        super.setToolBar(applicationTemplate);
        // create screenshot toolBar button (and disable screenshot button initially)
        scrnshotButton = setToolbarButton(scrnshoticonPath, "Take a screenshot", true);
        // add screenshot toolBar button to list of pre-existing toolBar buttons on the toolBar
        toolBar.getItems().add(scrnshotButton);
    }

    @Override
    protected void setToolbarHandlers(ApplicationTemplate applicationTemplate) {
        applicationTemplate.setActionComponent(new AppActions(applicationTemplate));
        newButton.setOnAction(e -> applicationTemplate.getActionComponent().handleNewRequest());
        saveButton.setOnAction(e -> applicationTemplate.getActionComponent().handleSaveRequest());
        loadButton.setOnAction(e -> applicationTemplate.getActionComponent().handleLoadRequest());
        exitButton.setOnAction(e -> applicationTemplate.getActionComponent().handleExitRequest());
        printButton.setOnAction(e -> applicationTemplate.getActionComponent().handlePrintRequest());
    }

    @Override
    public void initialize() {
        layout();
        setWorkspaceActions();
    }

    @Override
    public void clear() {
        // TODO for homework 1
        // clear data from data processor instance
        dataProcessor.clear();
    }

    private void layout() {
        // TODO for homework 1
        // necessary axis declarations (with initial scale and range/domain params set)
        NumberAxis horizontalAxis = new NumberAxis(0, 110, 10);
        NumberAxis verticalAxis = new NumberAxis(0, 100, 10); 
        
        // declare/initialize UI objects to be included in the first column
        Label dataFileLabel = new Label("Data File");
        dataFileLabel.setFont(Font.font(null, FontWeight.BOLD, 18));
        textArea = new TextArea();
        displayButton = new Button("Display");
        
        // create first column
        VBox vbox0 = new VBox();
        vbox0.setPrefWidth(windowWidth * .35);
        // add elements to first column
        vbox0.getChildren().addAll(dataFileLabel,textArea,displayButton);
        // align and space UI objects within the column
        vbox0.setAlignment(Pos.TOP_CENTER);
        vbox0.setSpacing(10);
        vbox0.setPadding(new Insets(10, 0, 10, 20));
        
        // declare/initialize UI objects to be included in the second column
        Label dataVisLabel = new Label("Data Visualization");
        dataVisLabel.setFont(Font.font(null, FontWeight.BOLD, 18));
        chart = new ScatterChart<>(horizontalAxis,verticalAxis);
        
        // create second column
        VBox vbox1 = new VBox();
        vbox1.setPrefWidth(windowWidth * .65);
        // add elements to second column
        vbox1.getChildren().addAll(dataVisLabel, chart);
        // align and space UI objects within the column
        vbox1.setAlignment(Pos.TOP_CENTER);
        vbox1.setPadding(new Insets(10, 20, 0, 0));
        
        // create a pane to hold both columns
        HBox hbox = new HBox();
        // add both columns (vbox0 and vbox1) to the HBox pane
        hbox.getChildren().addAll(vbox0, vbox1);
        
        // add the pane containing both columns to inside of pre-existing VBox root pane
        appPane.getChildren().add(hbox);
    }

    private void setWorkspaceActions() {
        // TODO for homework 1
        // initialize data processor instance 
        dataProcessor = new TSDProcessor();
    }
}
