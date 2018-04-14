// Aaron Knoll
package ui;

import actions.AppActions;
import dataprocessors.AppData;
import static java.io.File.separator;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ListIterator;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    private LineChart<Number, Number> chart;    // the chart where data will be displayed (LineChart version of original chart)

    private Button runButton;                   // workspace button to display data on the chart
    private TextArea textArea;                  // text area for new data input
    private boolean hasNewText;                 // whether or not the text area has any new data since last display

    private String scrnshoticonPath;            // relative (partial) path to SCREENSHOT_ICON
    private String runConfigIconPath;           // relative (partial) path to RUNCONFIG_ICON
    private ArrayList<String> firstTenLines;    // lines of data to be displayed in the TextArea
    private ArrayList<String> restOfTheLines;   // lines of data that are to replenish the TextArea

    private ToggleButton editToggle;            // toggle for edit/done functionality
    private ChoiceBox selectAlgType;            // drop-down for algorithm type selection
    HBox algOptionsPane;                        // pane to show different algorithms for a specified type
    private RadioButton alg1;                   // radio button for dummy algorithm
    private Button configAlgBtn;                // button to open algorithm configuration window
    private final VBox leftColumn = new VBox(); // create first column
    
    private Label inputDataDetails;
    HBox editTogglePane = new HBox();
    
    public LineChart<Number, Number> getChart() {
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
        // clear contents of textArea and scatter chart
        textArea.clear();
        chart.getData().clear();
        // DO NOT allow the user to take a screenshot of an empty chart
        scrnshotButton.setDisable(true);
        // disable new and save buttons upon clearing textArea and chart
        newButton.setDisable(true);
        disableSaveButton();
        // no new data to be displayed as there is NO DATA in textArea
        hasNewText = false;
        // clear data contained in the ArrayLists
        firstTenLines = null;
        restOfTheLines = null;
    }

    private void layout() {
        PropertyManager manager = applicationTemplate.manager;
        // declare/initialize UI objects to be included in the first column
        Label dataFileLabel = new Label(manager.getPropertyValue(DATA_FILE_LABEL_TEXT.name()));
        dataFileLabel.setFont(Font.font(null, FontWeight.BOLD, 18));
        dataFileLabel.setPadding(new Insets(0, 0, -35, 0));
        textArea = new TextArea();
        runButton = new Button("Run");
//        runButton = new Button(manager.getPropertyValue(DISPLAY_BUTTON_TEXT.name()));
       
        editToggle = new ToggleButton("Done");
        editToggle.setSelected(true);
        
        editTogglePane.setPadding(new Insets(0, 0, -9, 0));
        editTogglePane.setAlignment(Pos.CENTER_RIGHT);
        editTogglePane.getChildren().add(editToggle);
        
        inputDataDetails = new Label();
        inputDataDetails.setWrapText(true);

        selectAlgType = new ChoiceBox(FXCollections.observableArrayList("Algorithm Type", "Classification", "Clustering"));
        selectAlgType.getSelectionModel().selectFirst();
        
        ToggleGroup group = new ToggleGroup();

        alg1 = new RadioButton("Random Classification");
        alg1.setToggleGroup(group);
        alg1.setSelected(true);
        
        Image imageOk = new Image(runConfigIconPath);
        configAlgBtn = new Button("", new ImageView(imageOk));

//        RadioButton alg2 = new RadioButton("Random Classification 2");
//        alg2.setToggleGroup(group);
        
        leftColumn.setPrefWidth(windowWidth * .35);
        // add elements to first column
//        leftColumn.getChildren().addAll(dataFileLabel, editTogglePane, textArea, inputDataDetails, selectAlgType, alg1,runButton);
        leftColumn.getChildren().add(dataFileLabel);
        leftColumn.getChildren().add(editTogglePane);
        leftColumn.getChildren().add(textArea);
        leftColumn.getChildren().add(inputDataDetails);
        leftColumn.getChildren().add(selectAlgType);
        // TODO:    iterate over ArrayList of algorithms for the given algorithm type
        //          and add the elements to the column
        algOptionsPane = new HBox();
//        algOptionsPane.setPadding(new Insets(0, 0, -9, 0));
        algOptionsPane.setAlignment(Pos.CENTER_LEFT);
        alg1.setPadding(new Insets(0, 0, 0, 5));
        algOptionsPane.getChildren().add(configAlgBtn);
        algOptionsPane.getChildren().add(alg1);
        algOptionsPane.setVisible(false);
        leftColumn.getChildren().add(algOptionsPane);
        leftColumn.getChildren().add(runButton);

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

        // create second column
        VBox rightColumn = new VBox();
        rightColumn.setPrefWidth(windowWidth * .65);
        // add elements to second column
        rightColumn.getChildren().addAll(dataVisLabel, chart);
        // align and space UI objects within the column
        rightColumn.setAlignment(Pos.TOP_CENTER);
        rightColumn.setPadding(new Insets(10, 20, 0, 0));

        // create a pane to hold both columns
        HBox hbox = new HBox();
        // add both columns (leftColumn and rightColumn) to the HBox pane
        hbox.getChildren().addAll(leftColumn, rightColumn);

        // add the pane containing both columns to inside of pre-existing VBox root pane
        appPane.getChildren().add(hbox);
        
        // add custom style to application
        getPrimaryScene().getStylesheets().add("gui/css/data-vilij.css");
//        super.getPrimaryScene().setCursor(Cursor.WAIT);
//        getPrimaryScene().getRoot().setCursor(Cursor.WAIT);
//        System.out.println(super.getPrimaryScene().getStylesheets());
    }

    private void setWorkspaceActions() {
        hasNewText = false;

        configAlgBtn.setOnAction(e -> ((AppActions) applicationTemplate.getActionComponent()).configAlgorithm());
        
        selectAlgType.setOnAction(e -> {
            switch (selectAlgType.getSelectionModel().getSelectedIndex()) {
                case 1:
                    alg1.setText("Random Classification");
                    algOptionsPane.setVisible(true);
                    break;
                case 2:
                    alg1.setText("Random Clustering");
                    algOptionsPane.setVisible(true);
                    break;
                default:
                    algOptionsPane.setVisible(false);
                    break;
            }
        });

        editToggle.setOnAction(e -> {
            if (editToggle.isSelected()) {
                textArea.setDisable(false);
                editToggle.setText("Done");
            } else {
                textArea.setDisable(true);
                editToggle.setText("Edit");
            }
        });

        // when run button is clicked...
        runButton.setOnAction(e -> {
            if (hasNewText) {
                // clear scatter chart immediately before plotting new data
                chart.getData().clear();
                // DO NOT allow the user to take a screenshot of an empty chart
                scrnshotButton.setDisable(true);

                String strToBeProcessed = textArea.getText();
                if (restOfTheLines != null) {
                    ListIterator<String> itr = restOfTheLines.listIterator();
                    while (itr.hasNext()) {
                        strToBeProcessed += "\n" + itr.next();
                    }
                }
                // load data into the data processor...
                ((AppData) applicationTemplate.getDataComponent()).loadData(strToBeProcessed);
                // if data is plotted...
                if (!chart.getData().isEmpty()) {
                    // allow the user to take a screenshot of the chart
                    scrnshotButton.setDisable(false);
                }
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
            } else {
                newButton.setDisable(false);
                saveButton.setDisable(false);
                // new data that can potentially be displayed, by virtue of there being a newly typed character
                hasNewText = true;
            }
            // print # lines of data in TextArea (for debugging purposes)
//            System.out.println(textArea.getText().split("\n", -1).length);

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
        editTogglePane.setVisible(false);
        leftColumn.setVisible(true);
        textArea.setDisable(true);
        
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
                + dataFilePath.getFileName() + " . The labels are:";
        
        Iterator<String> labelItr = uniqueDataLabels.iterator();
        while (labelItr.hasNext()) {
            detailsStr += "\n-" + labelItr.next();
        }
        
        inputDataDetails.setText(detailsStr);
    }
}
