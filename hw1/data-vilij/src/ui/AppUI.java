package ui;

import actions.AppActions;
import dataprocessors.AppData;
import static java.io.File.separator;
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
import static settings.AppPropertyTypes.*;
import vilij.propertymanager.PropertyManager;
import static vilij.settings.PropertyTypes.*;
import vilij.templates.ApplicationTemplate;
import vilij.templates.UITemplate;

/**
 * This is the application's user interface implementation.
 *
 * @author Ritwik Banerjee
 */
public final class AppUI extends UITemplate {

    /**
     * The application to which this class of actions belongs.
     */
    ApplicationTemplate applicationTemplate;

    @SuppressWarnings("FieldCanBeLocal")
    private Button scrnshotButton;              // toolbar button to take a screenshot of the data
    private ScatterChart<Number, Number> chart; // the chart where data will be displayed
    private Button displayButton;               // workspace button to display data on the chart
    private TextArea textArea;                  // text area for new data input
    private boolean hasNewText;                 // whether or not the text area has any new data since last display

    private String scrnshoticonPath;            // relative (partial) path to SCREENSHOT_ICON

    public ScatterChart<Number, Number> getChart() {
        return chart;
    }

    public AppUI(Stage primaryStage, ApplicationTemplate applicationTemplate) {
        super(primaryStage, applicationTemplate);
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    protected void setResourcePaths(ApplicationTemplate applicationTemplate) {
        super.setResourcePaths(applicationTemplate);
        // set rel resource path for SCREENSHOT_ICON
        PropertyManager manager = applicationTemplate.manager;
        String iconsPath = "/" + String.join(separator,
                                             manager.getPropertyValue(GUI_RESOURCE_PATH.name()),
                                             manager.getPropertyValue(ICONS_RESOURCE_PATH.name()));
                                         
        scrnshoticonPath = String.join(separator, iconsPath, manager.getPropertyValue(SCREENSHOT_ICON.name()));
    }

    @Override
    protected void setToolBar(ApplicationTemplate applicationTemplate) {
        // TODO for homework 1
        // utilize super class method call for all but the final toolBarButton
        super.setToolBar(applicationTemplate);
        PropertyManager manager = applicationTemplate.manager;
        // create screenshot toolBar button (and disable screenshot button initially)
        scrnshotButton = setToolbarButton(scrnshoticonPath, manager.getPropertyValue(SCREENSHOT_TOOLTIP.name()), true);
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
        // clear contents of textArea and scatter chart
        textArea.clear();
        chart.getData().clear();
        // disable new and save buttons upon clearing textArea and chart
        newButton.setDisable(true);
        saveButton.setDisable(true);
        // no new data to be displayed as there is NO DATA in textArea
        hasNewText = false;
    }

    private void layout() {
        // TODO for homework 1
        PropertyManager manager = applicationTemplate.manager;
        // declare/initialize UI objects to be included in the first column
        Label dataFileLabel = new Label(manager.getPropertyValue(DATA_FILE_LABEL_TEXT.name()));
        dataFileLabel.setFont(Font.font(null, FontWeight.BOLD, 18));
        textArea = new TextArea();
        displayButton = new Button(manager.getPropertyValue(DISPLAY_BUTTON_TEXT.name()));

        // create first column
        VBox vbox0 = new VBox();
        vbox0.setPrefWidth(windowWidth * .35);
        // add elements to first column
        vbox0.getChildren().addAll(dataFileLabel, textArea, displayButton);
        // align and space UI objects within the column
        vbox0.setAlignment(Pos.TOP_CENTER);
        vbox0.setSpacing(10);
        vbox0.setPadding(new Insets(10, 0, 10, 20));

        // declare/initialize UI objects to be included in the second column
        Label dataVisLabel = new Label(manager.getPropertyValue(GRAPH_LABEL_TEXT.name()));
        dataVisLabel.setFont(Font.font(null, FontWeight.BOLD, 18));
        // initialize new scatter chart with unspecified axis ranges/tick values for automatic scaling
        chart = new ScatterChart<>(new NumberAxis(), new NumberAxis());

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
        hasNewText = false;

        // when display button is clicked...
        displayButton.setOnAction(e -> {
            if (hasNewText) {
                // clear scatter chart immediately before plotting new data
                chart.getData().clear();
                // load data from textArea into the data processor...
                ((AppData) applicationTemplate.getDataComponent()).loadData(textArea.getText());
                // and plot data
                ((AppData) applicationTemplate.getDataComponent()).displayData();
                // clear data from data processor (maybe optional???)
                ((AppData) applicationTemplate.getDataComponent()).clear();
                hasNewText = false;
            }

        });

        textArea.setOnKeyReleased(e -> {
            // if the textArea is empty...
            if (textArea.getText().trim().isEmpty()) {
                // keep new and save buttons disabled
                newButton.setDisable(true);
                saveButton.setDisable(true);
                // no new data to be displayed as there is NO DATA in textArea
                hasNewText = false;
                // if a newline character exists in the contents of textArea
                // (at least one line exists and has been terminated)...
            } else if (textArea.getText().indexOf('\n') != -1) {
                // enable the new and save buttons
                newButton.setDisable(false);
                saveButton.setDisable(false);
            } else {
                // new data that can potentially be displayed, by virtue of there being a newly typed character
                hasNewText = true;
            }
        });
    }

    /**
     * Public getter method for the contents of textArea.
     *
     * @return contents of textArea as a string with no leading or trailing
     * white space
     */
    public String getTextAreaData() {
        return textArea.getText().trim();
    }
}
