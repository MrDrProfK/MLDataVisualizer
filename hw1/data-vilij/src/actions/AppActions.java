package actions;

import java.io.File;
import java.io.FileWriter;
import vilij.components.ActionComponent;
import vilij.templates.ApplicationTemplate;

import java.io.IOException;
import java.nio.file.Path;
import javafx.application.Platform;
import javafx.stage.FileChooser;
import ui.AppUI;
import vilij.components.ConfirmationDialog;
import vilij.components.ConfirmationDialog.Option;
import vilij.components.Dialog;

/**
 * This is the concrete implementation of the action handlers required by the
 * application.
 *
 * @author Ritwik Banerjee
 */
public final class AppActions implements ActionComponent {

    /**
     * The application to which this class of actions belongs.
     */
    private ApplicationTemplate applicationTemplate;

    /**
     * Path to the data file currently active.
     */
    Path dataFilePath;

    public AppActions(ApplicationTemplate applicationTemplate) {
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    public void handleNewRequest() {
        // TODO for homework 1
        // try to prompt user to save current work before clearing and resetting data
        try {
            if (promptToSave()) {
                applicationTemplate.getUIComponent().clear();
            }
        } catch (IOException promptException) {
            applicationTemplate.getDialog(Dialog.DialogType.ERROR)
                    .show("Data Not Saved.", promptException.getLocalizedMessage());
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
     * This helper method verifies that the user really wants to save their
     * unsaved work, which they might not want to do. The user will be presented
     * with three options:
     * <ol>
     * <li><code>yes</code>, indicating that the user wants to save the work and
     * continue with the action,</li>
     * <li><code>no</code>, indicating that the user wants to continue with the
     * action without saving the work, and</li>
     * <li><code>cancel</code>, to indicate that the user does not want to
     * continue with the action, but also does not want to save the work at this
     * point.</li>
     * </ol>
     *
     * @return <code>false</code> if the user presses the <i>cancel</i>, and
     * <code>true</code> otherwise.
     */
    private boolean promptToSave() throws IOException {
        // TODO for homework 1
        // TODO remove the placeholder line below after you have implemented this method
        ConfirmationDialog confirmationDialog = (ConfirmationDialog)applicationTemplate.getDialog(Dialog.DialogType.CONFIRMATION);
        confirmationDialog.show("Save Current Work", "Would you like to save current work?");
        
        // analyze the dialog button clicked
        if (confirmationDialog.getSelectedOption() == Option.YES) {
            FileChooser fileChooser = new FileChooser();

            // create and add FileChooser ExtensionFilter for Tab-Separated Data Files (*.tsd)
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Tab-Separated Data File (*.tsd)", "*.tsd");
            fileChooser.getExtensionFilters().add(extFilter);

            File file = fileChooser.showSaveDialog(applicationTemplate.getUIComponent().getPrimaryWindow());

            // create and write to new file if file is NOT null
            try (FileWriter fileWriter = new FileWriter(file)) {
                // write contents of textArea to file
                fileWriter.write(((AppUI) applicationTemplate.getUIComponent()).getTextAreaData());
            } catch (Exception e) {
                throw new IOException("Data was not saved to disk.", e);
            }
        } else if (confirmationDialog.getSelectedOption() == Option.CANCEL) {
            // return false for CANCEL button click
            return false;
        }
        // return true for both YES and NO button clicks
        return true;
    }
}
