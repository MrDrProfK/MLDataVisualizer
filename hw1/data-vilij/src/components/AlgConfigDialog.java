// Aaron Knoll
package components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;

/**
 *
 * @author aaronknoll
 */
public class AlgConfigDialog extends Stage {

    public enum Option {

        OK("OK"), CANCEL("Cancel");

        @SuppressWarnings("unused")
        private String option;

        Option(String option) {
            this.option = option;
        }
    }

    private static AlgConfigDialog dialog;
    private AlgorithmConfiguration newAlgConfig;

    private Option selectedOption;
    private TextField maxIterationsField;
    private TextField updateInterval;
    private final Label clusterLabel = new Label("# of Clusters:");
    private TextField numOfClusteringLabels;
    private CheckBox continuousRun;

    private AlgConfigDialog() {
        /* empty constructor */ }

    public static AlgConfigDialog getDialog() {
        if (dialog == null) {
            dialog = new AlgConfigDialog();
        }
        return dialog;
    }

    /**
     * Completely initializes the error dialog to be used.
     *
     * @param owner the window on top of which the error dialog window will be
     * displayed
     */
    public void init(Stage owner) {
        initModality(Modality.WINDOW_MODAL); // modal => messages are blocked from reaching other windows
        initOwner(owner);

        List<Button> buttons = Arrays.asList(new Button(Option.OK.name()),
                new Button(Option.CANCEL.name()));

        buttons.forEach(button -> button.setOnAction(e -> {
            this.selectedOption = Option.valueOf(((Button) e.getSource()).getText());
            this.hide();
        }));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        maxIterationsField = new TextField();
        updateInterval = new TextField();
        numOfClusteringLabels = new TextField();
        continuousRun = new CheckBox("Continuous Run");

        continuousRun.setIndeterminate(false);

        grid.add(new Label("Max. Iterations:"), 0, 0);
        grid.add(maxIterationsField, 1, 0);
        grid.add(new Label("Update Interval:"), 0, 1);
        grid.add(updateInterval, 1, 1);
        grid.add(clusterLabel, 0, 2);
        grid.add(numOfClusteringLabels, 1, 2);
        grid.add(continuousRun, 1, 3);

        HBox buttonBox = new HBox(5);
        buttonBox.getChildren().addAll(buttons);

        VBox messagePane = new VBox(grid, buttonBox);
        messagePane.setAlignment(Pos.CENTER);
        messagePane.setPadding(new Insets(10, 20, 20, 20));
        messagePane.setSpacing(10);

        this.setScene(new Scene(messagePane));
    }

    /**
     *
     * @param algConfig
     * @return
     */
    public boolean show(AlgorithmConfiguration algConfig) {

        // set the title of the dialog
        setTitle("Algorithm Run Configuration");

        this.maxIterationsField.setText(Integer.toString(algConfig.maxIterations));
        this.updateInterval.setText(Integer.toString(algConfig.updateInterval));
        this.continuousRun.setSelected(algConfig.continuousRun == true);

        clusterLabel.setVisible(algConfig.isClustering());
        this.numOfClusteringLabels.setVisible(algConfig.isClustering());
        this.numOfClusteringLabels.setText(Integer.toString(algConfig.numOfClusteringLabels));

        newAlgConfig = new AlgorithmConfiguration(algConfig.maxIterations,
                algConfig.updateInterval, algConfig.continuousRun,
                algConfig.isClustering(), algConfig.numOfClusteringLabels);

        this.maxIterationsField.setOnKeyPressed(key -> {
            if (key.getCode() == KeyCode.TAB || key.getCode() == KeyCode.ENTER) {
                validateMaxIterationsField();
            }
        });

        this.updateInterval.setOnKeyPressed(key -> {
            if (key.getCode() == KeyCode.TAB || key.getCode() == KeyCode.ENTER) {
                validateUpdateInterval();
            }
        });

        this.numOfClusteringLabels.setOnKeyPressed(key -> {
            if (key.getCode() == KeyCode.TAB || key.getCode() == KeyCode.ENTER) {
                validateNumOfClusteringLabels();
            }
        });
        // open the dialog and wait for the user to click the close button
        showAndWait();

        if (selectedOption == Option.OK) {
            validateMaxIterationsField();
            algConfig.maxIterations = newAlgConfig.maxIterations;
            validateUpdateInterval();
            algConfig.updateInterval = newAlgConfig.updateInterval;
            algConfig.continuousRun = newAlgConfig.continuousRun;
            
            if (algConfig.clustering) {

                validateNumOfClusteringLabels();
                algConfig.numOfClusteringLabels = newAlgConfig.numOfClusteringLabels;
            }

            return true;
        } else {
            return false;
        }
    }

    private void validateMaxIterationsField() {
        try {
            newAlgConfig.maxIterations = Integer.valueOf(this.maxIterationsField.getText());
            if (newAlgConfig.maxIterations < 1) {
                newAlgConfig.maxIterations = 1;
                this.maxIterationsField.setText(Integer.toString(newAlgConfig.maxIterations));
            }
        } catch (NumberFormatException ex) {
            this.maxIterationsField.setText(Integer.toString(newAlgConfig.maxIterations));
        }
    }

    private void validateUpdateInterval() {
        try {
            newAlgConfig.updateInterval = Integer.valueOf(this.updateInterval.getText());
            if (newAlgConfig.updateInterval < 1) {
                newAlgConfig.updateInterval = 1;
                this.updateInterval.setText(Integer.toString(newAlgConfig.updateInterval));
            }
        } catch (NumberFormatException ex) {
            this.updateInterval.setText(Integer.toString(newAlgConfig.updateInterval));
        }
    }

    private void validateNumOfClusteringLabels() {
        try {
            newAlgConfig.numOfClusteringLabels = Integer.valueOf(this.numOfClusteringLabels.getText());
            if (newAlgConfig.numOfClusteringLabels < 1) {
                newAlgConfig.numOfClusteringLabels = 1;
                this.numOfClusteringLabels.setText(Integer.toString(newAlgConfig.numOfClusteringLabels));
            }
        } catch (NumberFormatException ex) {
            this.numOfClusteringLabels.setText(Integer.toString(newAlgConfig.numOfClusteringLabels));
        }
    }
}
