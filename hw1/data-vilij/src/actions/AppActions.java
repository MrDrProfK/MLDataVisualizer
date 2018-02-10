package actions;

import java.io.File;
import java.io.FileWriter;
import vilij.components.ActionComponent;
import vilij.templates.ApplicationTemplate;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;

/**
 * This is the concrete implementation of the action handlers required by the application.
 *
 * @author Ritwik Banerjee
 */
public final class AppActions implements ActionComponent {

    /** The application to which this class of actions belongs. */
    private ApplicationTemplate applicationTemplate;

    /** Path to the data file currently active. */
    Path dataFilePath;

    public AppActions(ApplicationTemplate applicationTemplate) {
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    public void handleNewRequest() {
        // TODO for homework 1
        // try to prompt user to save current work before clearing and resetting data
        try {
            promptToSave();
        } catch (IOException promptException) {
            System.out.println(promptException.toString());
        }
    }

    @Override
    public void handleSaveRequest() {
        // TODO: NOT A PART OF HW 1
    }

    @Override
    public void handleLoadRequest() {
        // TODO: NOT A PART OF HW 1
    }

    @Override
    public void handleExitRequest() {
        // TODO for homework 1
        Platform.exit();
    }

    @Override
    public void handlePrintRequest() {
        // TODO: NOT A PART OF HW 1
    }

    public void handleScreenshotRequest() throws IOException {
        // TODO: NOT A PART OF HW 1
    }

    /**
     * This helper method verifies that the user really wants to save their unsaved work, which they might not want to
     * do. The user will be presented with three options:
     * <ol>
     * <li><code>yes</code>, indicating that the user wants to save the work and continue with the action,</li>
     * <li><code>no</code>, indicating that the user wants to continue with the action without saving the work, and</li>
     * <li><code>cancel</code>, to indicate that the user does not want to continue with the action, but also does not
     * want to save the work at this point.</li>
     * </ol>
     *
     * @return <code>false</code> if the user presses the <i>cancel</i>, and <code>true</code> otherwise.
     */
    private boolean promptToSave() throws IOException {
        // TODO for homework 1
        // TODO remove the placeholder line below after you have implemented this method
        // create and configure dialog box asking whether or not to save current work
        Alert saveWorkDialogBox = new Alert(AlertType.CONFIRMATION);
        saveWorkDialogBox.setTitle("Save Current Work");
        saveWorkDialogBox.setHeaderText(null);
        saveWorkDialogBox.setContentText("Would you like to save current work?");
        
        // create YES, NO, and CANCEL buttons for dialog box
        ButtonType buttonTypeYes = new ButtonType("YES");
        ButtonType buttonTypeNo = new ButtonType("NO");
        ButtonType buttonTypeCancel = new ButtonType("CANCEL", ButtonData.CANCEL_CLOSE);

        // add YES, NO, and CANCEL buttons to dialog box
        saveWorkDialogBox.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo, buttonTypeCancel);

        // gather and analyze the dialog button clicked
        Optional<ButtonType> result = saveWorkDialogBox.showAndWait();
        if (result.get() == buttonTypeYes) {
            FileChooser fileChooser = new FileChooser();
            
            // create and add FileChooser ExtensionFilter for Tab-Separated Data Files (*.tsd)
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Tab-Separated Data File (*.tsd)", "*.tsd");
            fileChooser.getExtensionFilters().add(extFilter);
            
            File file = fileChooser.showSaveDialog(applicationTemplate.getUIComponent().getPrimaryWindow());
           
            // create and write to new file if file is NOT null
            if (file != null){
                try {
                    FileWriter fileWriter = null;

                    fileWriter = new FileWriter(file);
                    // write filler text to file for testing purposes
                    fileWriter.write("filler");
                    fileWriter.close();
                } catch (IOException ex) {
                    System.out.println(ex.toString());
                }
            }
            
        } else if(result.get() == buttonTypeCancel) {
            // return false for CANCEL button click
            return false;
        }
        // return true for both YES and NO button clicks
        return true;
    }
}
