// Aaron Knoll
package ui;

import actions.AppActions;
import algorithms.Algorithm;
import algorithms.AlgorithmPauser;
//import classification.*;
//import clustering.*;
import components.AlgorithmConfiguration;
import data.DataSet;
import dataprocessors.AppData;
import java.awt.image.RenderedImage;
import java.io.File;
import static java.io.File.separator;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
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
public final class AppUI extends UITemplate implements AlgResourcePreparer {

    /**
     * The application to which this class of actions belongs.
     */
    ApplicationTemplate applicationTemplate;

    @SuppressWarnings("FieldCanBeLocal")
    private Button scrnshotButton;                  // toolbar button to take a screenshot of the data
    private LineChart<Number, Number> chart;        // the chart where data will be displayed (LineChart version of original chart)

    private Button runPauseBtn;                     // workspace button to display data on the chart
    private TextArea textArea;                      // text area for new data input
    private boolean hasNewText;                     // whether or not the text area has any new data since last display

    private String scrnshoticonPath;                // relative (partial) path to SCREENSHOT_ICON
    private String runConfigIconPath;               // relative (partial) path to RUNCONFIG_ICON
    private ArrayList<String> firstTenLines;        // lines of data to be displayed in the TextArea
    private ArrayList<String> restOfTheLines;       // lines of data that are to replenish the TextArea

    private Button editToggle;                      // toggle for edit/done functionality
    private ChoiceBox selectAlgType;                // drop-down for algorithm type selection
    
    private final VBox leftColumn = new VBox();     // create first column
    private VBox algorithmPane;
    private Label inputDataDetails;                 // 
    private final HBox editTogglePane = new HBox(); // 
    private ToggleGroup group;                      //
    private Path dataFilePath;                      //
    private AlgorithmConfiguration currentAlgConfig;//
    private String currentAlgPrettyName;
    private Label algNotificationLabel;
    
    private DataSet dataset;
    private AlgorithmPauser pauser = new AlgorithmPauser();
    private Thread algThread;
    
    @Override
    public LineChart<Number, Number> getChart() {
        return chart;
    }

    @Override
    public AlgorithmPauser getPauser() {
        return pauser;
    }

    @Override
    public Label getAlgNotificationLabel() {
        return algNotificationLabel;
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
        runConfigIconPath = String.join(separator, iconsPath, "run-config.png");
    }

    @Override
    protected void setToolBar(ApplicationTemplate applicationTemplate) {
        // utilize super class method call for all but the final toolBarButton
        super.setToolBar(applicationTemplate);
        PropertyManager manager = applicationTemplate.manager;
        newButton.setDisable(false);
        // create screenshot toolBar button (and disable screenshot button initially)
        scrnshotButton = setToolbarButton(scrnshoticonPath, manager.getPropertyValue(SCREENSHOT_TOOLTIP.name()), true);
        // add screenshot toolBar button to list of pre-existing toolBar buttons on the toolBar
        toolBar.getItems().add(scrnshotButton);
    }

    @Override
    protected void setToolbarHandlers(ApplicationTemplate applicationTemplate) {
//        applicationTemplate.setActionComponent(new AppActions(applicationTemplate));
        newButton.setOnAction(e -> applicationTemplate.getActionComponent().handleNewRequest());
        saveButton.setOnAction(e -> applicationTemplate.getActionComponent().handleSaveRequest());
        loadButton.setOnAction(e -> applicationTemplate.getActionComponent().handleLoadRequest());
        exitButton.setOnAction(e -> applicationTemplate.getActionComponent().handleExitRequest());
        printButton.setOnAction(e -> applicationTemplate.getActionComponent().handlePrintRequest());
        scrnshotButton.setOnAction(e -> {
            try {
                ((AppActions) applicationTemplate.getActionComponent()).handleScreenshotRequest();
            } catch (IOException ex) {
                Logger.getLogger(AppUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    @Override
    public void initialize() {
        layout();
        setWorkspaceActions();
    }

    @Override
    public void clear() {
        // clear contents of TextArea and allow for initial editing
        textArea.clear();
        textArea.setDisable(false);
        // clear contents of Label that displays details of the inputted dataset
        inputDataDetails.setText("");
        // clear contents of scatter chart
        chart.getData().clear();
        algNotificationLabel.setText("");
        // DO NOT allow the user to take a screenshot of an empty chart
        scrnshotButton.setDisable(true);
        // disable new and save buttons upon clearing textArea and chart
        newButton.setDisable(true);
        disableSaveButton();
        // no new data to be displayed as there is NO DATA in textArea
        hasNewText = false;
        algThread = null;
        // clear data contained in the ArrayLists
        firstTenLines = null;
        restOfTheLines = null;
        // clear HashMap data containing custom algorithm
        ((AppActions) applicationTemplate.getActionComponent()).clear();
        
        if (group.getSelectedToggle() != null) {

            ((RadioButton) group.getSelectedToggle()).setSelected(false);
        }

        runPauseBtn.setVisible(false);

        if (selectAlgType.getItems().size() < 3) {
            selectAlgType.getItems().add(1, "Classification");
        }
        
        dataFilePath = null;
    }

    private void layout() {
        PropertyManager manager = applicationTemplate.manager;
        scrnshotButton.setDisable(true);
        // declare/initialize UI objects to be included in the first column
        Label dataFileLabel = new Label(manager.getPropertyValue(DATA_FILE_LABEL_TEXT.name()));
        dataFileLabel.setFont(Font.font(null, FontWeight.BOLD, 18));
        dataFileLabel.setPadding(new Insets(0, 0, -35, 0));
        textArea = new TextArea();
        runPauseBtn = new Button("Run");
        runPauseBtn.setVisible(false);
       
        editToggle = new Button("Done");
        
        editTogglePane.setPadding(new Insets(0, 0, -9, 0));
        editTogglePane.setAlignment(Pos.CENTER_RIGHT);
        editTogglePane.getChildren().add(editToggle);
        
        inputDataDetails = new Label();
        inputDataDetails.setWrapText(true);

        selectAlgType = new ChoiceBox(FXCollections.observableArrayList("Algorithm Type", "Classification", "Clustering"));
        selectAlgType.getSelectionModel().selectFirst();
        
        leftColumn.setPrefWidth(windowWidth * .35);
        // add elements to first column
        leftColumn.getChildren().add(dataFileLabel);
        leftColumn.getChildren().add(editTogglePane);
        leftColumn.getChildren().add(textArea);
        leftColumn.getChildren().add(inputDataDetails);
        leftColumn.getChildren().add(selectAlgType);
        
        algorithmPane = new VBox();
        // TODO:    iterate over ArrayList of algorithms for the given algorithm 
        //          type and add the elements to the column
        // MOVED CODE
        
        group = new ToggleGroup();

        leftColumn.getChildren().addAll(algorithmPane, runPauseBtn);

        // align and space UI objects within the column
        leftColumn.setAlignment(Pos.TOP_CENTER);
        leftColumn.setSpacing(10);
        leftColumn.setPadding(new Insets(10, 0, 10, 20));
        // Only the data visualization chart and the toolbar should be visible upon application startup
        leftColumn.setVisible(false);

        // declare/initialize UI objects to be included in the second column
        Label dataVisLabel = new Label(manager.getPropertyValue(GRAPH_LABEL_TEXT.name()));
        dataVisLabel.setFont(Font.font(null, FontWeight.BOLD, 18));
        // initialize new scatter chart with unspecified axis ranges/tick values for automatic scaling
        chart = new LineChart<>(new NumberAxis(), new NumberAxis());
        chart.setAnimated(false);
        algNotificationLabel = new Label();

        // create second column
        VBox rightColumn = new VBox();
        rightColumn.setPrefWidth(windowWidth * .65);
        // add elements to second column
        rightColumn.getChildren().addAll(dataVisLabel, chart, algNotificationLabel);
        // align and space UI objects within the column
        rightColumn.setAlignment(Pos.TOP_CENTER);
        rightColumn.setPadding(new Insets(10, 20, 0, 0));

        // add both columns (leftColumn and rightColumn) to the HBox pane
        // add the pane containing both columns to inside of pre-existing VBox root pane
        appPane.getChildren().add(new HBox(leftColumn, rightColumn));
        
        // add custom style to application
        getPrimaryScene().getStylesheets().add("gui/css/data-vilij.css");
    }

    private void setWorkspaceActions() {
        hasNewText = false;
        
        selectAlgType.setOnAction(e -> {
            // clear HashMap data containing custom algorithm
//            ((AppActions) applicationTemplate.getActionComponent()).clear();
            
            if (group.getSelectedToggle() != null) {
                ((RadioButton) group.getSelectedToggle()).setSelected(false);
            }

            runPauseBtn.setVisible(false);
            algorithmPane.setVisible(false);
            algorithmPane.getChildren().clear();

            int algConfigSize;
            Iterator algNameItr;
            switch (selectAlgType.getSelectionModel().getSelectedItem().toString()) {
                case "Classification":
                    algConfigSize = ((AppActions) applicationTemplate.getActionComponent()).getAlgConfigs(0).size();
                    algNameItr = ((AppActions) applicationTemplate.getActionComponent()).getAlgConfigs(0).keySet().iterator();

                    for (int i = 0; i < algConfigSize; i++) {
                        HBox hb         = new HBox();
                        RadioButton rb  = new RadioButton(algNameItr.next().toString());

                        rb.setToggleGroup(group);
                        rb.setPadding(new Insets(0, 0, 0, 5));

                        // add new algorithm configuration settings button
                        hb.getChildren().add(new Button("", new ImageView(new Image(runConfigIconPath))));
                        hb.getChildren().add(rb);
                        hb.setAlignment(Pos.CENTER_LEFT);
                        
                        algorithmPane.getChildren().add(hb);
                    }
                    setClassificationConfigSectionControls();
                    algorithmPane.setVisible(true);
                    break;
                case "Clustering":
                    algConfigSize = ((AppActions) applicationTemplate.getActionComponent()).getAlgConfigs(1).size();
                    algNameItr = ((AppActions) applicationTemplate.getActionComponent()).getAlgConfigs(1).keySet().iterator();

                    for (int i = 0; i < algConfigSize; i++) {
                        HBox hb         = new HBox();
                        RadioButton rb  = new RadioButton(algNameItr.next().toString());

                        rb.setToggleGroup(group);
                        rb.setPadding(new Insets(0, 0, 0, 5));

                        // add new algorithm configuration settings button
                        hb.getChildren().add(new Button("", new ImageView(new Image(runConfigIconPath))));
                        hb.getChildren().add(rb);
                        hb.setAlignment(Pos.CENTER_LEFT);

                        algorithmPane.getChildren().add(hb);
                    }
                    setClusteringConfigSectionControls();
                    algorithmPane.setVisible(true);
                    break;
                default:
                    break;
            }
        });

        editToggle.setOnAction(e -> {
            if (editToggle.getText().compareTo("Done") == 0) {
                String strToBeProcessed = textArea.getText();
                if (restOfTheLines != null) {
                    ListIterator<String> itr = restOfTheLines.listIterator();
                    while (itr.hasNext()) {
                        strToBeProcessed += "\n" + itr.next();
                    }
                }
                // load data into the data processor...
                if (((AppData) applicationTemplate.getDataComponent()).loadData(strToBeProcessed)) {
                    dataset = DataSet.fromInputtedData(new ArrayList<>(Arrays.asList(strToBeProcessed.split("\n"))));
                    textArea.setDisable(true);
                    editToggle.setText("Edit");
                }
            } else {
                textArea.setDisable(false);
                editToggle.setText("Done");
            }
        });

        runPauseBtn.setOnAction(e -> {
         
            switch (runPauseBtn.getText()) {
                case "Run":
                    pauser.resume();
                    break;
                case "Pause":
                    pauser.pause();
                    break;
                default:
                    break;
            }
            alternateRunPause();

            if (algThread == null || !algThread.isAlive()) {
                // clear scatter chart immediately before plotting new data
                chart.getData().clear();

                try {
                    if (dataFilePath != null) {
                        dataset = DataSet.fromTSDFile(this.dataFilePath);
                    }
                    configureChartSettings();
                    // START PLOTTING ORIGINAL DATASET
                    ((AppData) applicationTemplate.getDataComponent()).displayData();
                    // END PLOTTING ORIGINAL DATASET
                    if(currentAlgConfig.isClustering()) {
                        try {
                            try {
                                algThread = new Thread((Algorithm)
                                ((AppActions) applicationTemplate.getActionComponent())
                                        .getAlgorithm(currentAlgPrettyName).getConstructor(DataSet.class,
                                                int.class,
                                                int.class,
                                                int.class,
                                                boolean.class,
                                                AlgResourcePreparer.class).newInstance(dataset, currentAlgConfig.maxIterations, currentAlgConfig.updateInterval, currentAlgConfig.numOfClusteringLabels, currentAlgConfig.continuousRun, this)
                                );
                            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                                Logger.getLogger(AppUI.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } catch (NoSuchMethodException | SecurityException ex) {
                            Logger.getLogger(AppUI.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    } else {
                        try {
                            try {
                                algThread = new Thread((Algorithm) ((AppActions) applicationTemplate.getActionComponent())
                                        .getAlgorithm(currentAlgPrettyName).getConstructor(DataSet.class,
                                                int.class,
                                                int.class,
                                                boolean.class,
                                                AlgResourcePreparer.class).newInstance(dataset, currentAlgConfig.maxIterations, currentAlgConfig.updateInterval, currentAlgConfig.continuousRun, this)
                                );
                            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                                Logger.getLogger(AppUI.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } catch (NoSuchMethodException | SecurityException ex) {
                            Logger.getLogger(AppUI.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
//                    System.out.println("max iter:" + currentAlgConfig.maxIterations + "\nupdate interval:" + currentAlgConfig.updateInterval + "\ncontinuous?:" + currentAlgConfig.continuousRun);
                    algThread.start();
                } catch (IOException ex) {
                    System.out.println(ex);
                }
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
            } else {
                newButton.setDisable(false);
                saveButton.setDisable(false);
                // new data that can potentially be displayed, by virtue of there being a newly typed character
                hasNewText = true;
            }

            // TODO: REVISIT LATER!!!
            if (restOfTheLines != null) {
                ListIterator<String> itr = restOfTheLines.listIterator();

                while (textArea.getText().split("\n", -1).length < 10 && itr.hasNext()) {
                    textArea.appendText("\n" + itr.next());
                    itr.remove();
                }
            }

        });
    }

    /**
     * Getter method for the retrieval of textArea contents.
     *
     * @return contents of textArea as a string with no leading or trailing
     * white space
     */
    public String getTextAreaData() {
        return textArea.getText().trim();
    }

    /**
     * Setter method used to disable saveButton.
     *
     */
    public void disableSaveButton() {
        saveButton.setDisable(true);
    }
    
    /**
     * Prepares the UI for new user-typed input.
     */
    public void prepareUIForUserTypedInput(){
        clear();
        editTogglePane.setVisible(true);
        leftColumn.setVisible(true);
        newButton.setDisable(true);
    }
    
    /**
     * Prepares and populates the UI for new input loaded from a file.
     * 
     * @param dataLoadedFromFile    data from loaded file
     * @param uniqueDataLabels      different labels that exist in the input dataset
     * @param dataFilePath          file path for input file
     */
    public void prepareUIForFileLoadedInput(ArrayList<String> dataLoadedFromFile, HashSet<String> uniqueDataLabels, Path dataFilePath){
        clear();
        editTogglePane.setVisible(false);
        leftColumn.setVisible(true);
        textArea.setDisable(true);
        this.dataFilePath = dataFilePath;
        
        firstTenLines = new ArrayList<>();
        restOfTheLines = new ArrayList<>();

        ListIterator<String> fileDataItr = dataLoadedFromFile.listIterator();
        if (dataLoadedFromFile.size() > 10) {
            for (int i = 0; i < 10; i++) {
                firstTenLines.add(fileDataItr.next());
            }
            while (fileDataItr.hasNext()) {
                restOfTheLines.add(fileDataItr.next());
            }
        } else {
            while (fileDataItr.hasNext()) {
                firstTenLines.add(fileDataItr.next());
            }
        }
        textArea.clear();
        ListIterator<String> itr = firstTenLines.listIterator();
        int i = 0;
        while (itr.hasNext()) {
            if (i < 9) {
                textArea.appendText(itr.next() + "\n");
            } else {
                textArea.appendText(itr.next());
            }
            i++;
        }
        if (!textArea.getText().trim().isEmpty()) {
            newButton.setDisable(false);
            hasNewText = true;
        }
        disableSaveButton();
        
        String detailsStr = dataLoadedFromFile.size() + " instances with "
                + uniqueDataLabels.size() + " labels loaded from "
                + dataFilePath + " . The labels are:";

        if (uniqueDataLabels.size() != 2) {
            // TODO: disable classification algorithm type
            selectAlgType.getItems().remove(1);
        }
        
        Iterator<String> labelItr = uniqueDataLabels.iterator();
        while (labelItr.hasNext()) {
            detailsStr += "\n-" + labelItr.next();
        }
        
        inputDataDetails.setText(detailsStr);
    }
    
    /**
     * Prepares UI for classification algorithm selection and configuration.
     */
    private void setClassificationConfigSectionControls(){
        algorithmPane.getChildren().forEach(p -> {
            ((Button) ((HBox) p).getChildren().get(0)).setOnAction(e -> {
                ((AppActions) applicationTemplate.getActionComponent())
                        .configAlgorithm(((RadioButton) ((HBox) p).getChildren().get(1)).getText());
                
                try {
                    if (((AppActions) applicationTemplate.getActionComponent())
                            .getAlgConfigs(0).get(((RadioButton) group.getSelectedToggle()).getText()) != null) {

                        currentAlgPrettyName = ((RadioButton) group.getSelectedToggle()).getText();
                        currentAlgConfig = ((AppActions) applicationTemplate.getActionComponent())
                                .getAlgConfigs(0).get(currentAlgPrettyName);
                        runPauseBtn.setVisible(true);
                    }
                } catch (NullPointerException npe) {
                    // do nothing except suppress the exception
                }
            });

            ((RadioButton) ((HBox) p).getChildren().get(1)).setOnAction(e -> {
                currentAlgPrettyName = ((RadioButton) group.getSelectedToggle()).getText();
                currentAlgConfig = ((AppActions) applicationTemplate.getActionComponent())
                        .getAlgConfigs(0).get(currentAlgPrettyName);
                if (currentAlgConfig != null) {
                    runPauseBtn.setVisible(true);
                } else {
                    runPauseBtn.setVisible(false);
                }
            });
        });
    }
    
    /**
     * Prepares UI for clustering algorithm selection and configuration.
     */
    private void setClusteringConfigSectionControls(){
        algorithmPane.getChildren().forEach(p -> {
            ((Button) ((HBox) p).getChildren().get(0)).setOnAction(e -> {
                ((AppActions) applicationTemplate.getActionComponent())
                        .configAlgorithm(((RadioButton) ((HBox) p).getChildren().get(1)).getText());
                try {
                    if (((AppActions) applicationTemplate.getActionComponent())
                            .getAlgConfigs(1).get(((RadioButton) group.getSelectedToggle()).getText()) != null) {

                        currentAlgPrettyName = ((RadioButton) group.getSelectedToggle()).getText();
                        currentAlgConfig = ((AppActions) applicationTemplate.getActionComponent())
                                .getAlgConfigs(1).get(currentAlgPrettyName);
                        runPauseBtn.setVisible(true);
                    }
                } catch (NullPointerException npe) {
                    // do nothing except suppress the exception
                }
            });
           
            ((RadioButton) ((HBox) p).getChildren().get(1)).setOnAction(e -> {
                currentAlgPrettyName = ((RadioButton) group.getSelectedToggle()).getText();
                currentAlgConfig = ((AppActions) applicationTemplate.getActionComponent())
                        .getAlgConfigs(1).get(currentAlgPrettyName);
                if (currentAlgConfig != null) {
                    runPauseBtn.setVisible(true);
                } else {
                    runPauseBtn.setVisible(false);
                }
            });
        });
    }
    
    /**
     * Overrides chart auto-scaling and appropriately prepares the chart for the
     * display of data and subsequent iterations of algorithms that are to 
     * analyze the data.
     */
    private void configureChartSettings() {
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
        
        NumberAxis xAxis = ((NumberAxis) chart.getXAxis());
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(xMin - (domainSize * .1));
        xAxis.setUpperBound(xMax + (domainSize * .1));
        xAxis.setTickUnit((xAxis.getUpperBound() - xAxis.getLowerBound()) / 10);

        NumberAxis yAxis = ((NumberAxis) chart.getYAxis());
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(yMin - (domainSize * .2));
        yAxis.setUpperBound(yMax + (domainSize * .2));
        yAxis.setTickUnit((yAxis.getUpperBound() - yAxis.getLowerBound()) / 5);
    }
    
    @Override
    public void alternateRunPause() {
        
        switch (runPauseBtn.getText()) {
            case "Run":
                scrnshotButton.setDisable(true);
                runPauseBtn.setText("Pause");
                break;
            case "Pause":
                scrnshotButton.setDisable(false);
                runPauseBtn.setText("Run");
                break;
            default:
                break;
        }
    }
}
