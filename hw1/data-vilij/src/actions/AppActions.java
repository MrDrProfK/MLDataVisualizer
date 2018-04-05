// Aaron Knoll
package actions;

import dataprocessors.AppData;
import java.io.FileWriter;
import vilij.components.ActionComponent;
import vilij.templates.ApplicationTemplate;

import java.io.IOException;
import java.nio.file.Path;
import javafx.application.Platform;
import javafx.stage.FileChooser;
import static settings.AppPropertyTypes.*;
import ui.AppUI;
import vilij.components.ConfirmationDialog;
import vilij.components.ConfirmationDialog.Option;
import vilij.components.Dialog;
import vilij.propertymanager.PropertyManager;

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
    
    /**
     * Indicates whether or not a new request is occurring for the first time
     * since application startup
     */
    private boolean firstNewRequest;

    public AppActions(ApplicationTemplate applicationTemplate) {
        this.applicationTemplate = applicationTemplate;
        firstNewRequest = true;
    }

    @Override
    public void handleNewRequest() {
        // if it's the first request to create new data since application startup...
        if(firstNewRequest) {
            // show left column
            ((AppUI) (applicationTemplate.getUIComponent())).prepareUIForUserTypedInput();
            firstNewRequest = false;
        } else {
            // try to prompt user to save current work before clearing and resetting data
            try {
                if (!firstNewRequest && promptToSave()) {
                    applicationTemplate.getUIComponent().clear();
                    dataFilePath = null;
                }
            } catch (IOException promptException) {
                PropertyManager manager = applicationTemplate.manager;
                applicationTemplate.getDialog(Dialog.DialogType.ERROR)
                        .show(manager.getPropertyValue(DATA_NOT_SAVED_WARNING_TITLE.name()), promptException.getLocalizedMessage());
            }
        }
    }

    @Override
    public void handleSaveRequest() {
        // TODO for homework 2
        PropertyManager manager = applicationTemplate.manager;

        FileChooser fileChooser = new FileChooser();

        // create and add FileChooser ExtensionFilter for Tab-Separated Data Files (*.tsd)
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(manager.getPropertyValue(DATA_FILE_EXT_DESC.name()),
                '*' + manager.getPropertyValue(DATA_FILE_EXT.name()));
        fileChooser.getExtensionFilters().add(extFilter);

        try {
            // only present Save As...Prompt if data has NOT been previously saved to a specified path
            if (dataFilePath == null) {
                dataFilePath = fileChooser.showSaveDialog(applicationTemplate.getUIComponent().getPrimaryWindow()).toPath();
            }

            ((AppData) (applicationTemplate.getDataComponent())).saveData(dataFilePath);
            
        } catch (NullPointerException npe) {
            // do nothing. save was aborted by user.
        }
    }

    @Override
    public void handleLoadRequest() {
        PropertyManager manager = applicationTemplate.manager;
        FileChooser fileChooser = new FileChooser();

        // create and add FileChooser ExtensionFilter for Tab-Separated Data Files (*.tsd)
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(manager.getPropertyValue(DATA_FILE_EXT_DESC.name()),
                '*' + manager.getPropertyValue(DATA_FILE_EXT.name()));
        fileChooser.getExtensionFilters().add(extFilter);

        try {
            // present open file dialog
            dataFilePath = fileChooser.showOpenDialog(applicationTemplate.getUIComponent().getPrimaryWindow()).toPath();
            ((AppData) (applicationTemplate.getDataComponent())).loadData(dataFilePath);
            
            ((AppUI) (applicationTemplate.getUIComponent())).prepareUIForFileLoadedInput();
        } catch (NullPointerException npe) {
            // do nothing. save was aborted by user.
        }
    }

    @Override
    public void handleExitRequest() {
        Platform.exit();
    }

    @Override
    public void handlePrintRequest() {
        // TODO: NOT A PART OF HW 1
    }

    public void handleScreenshotRequest() throws IOException {
        // TODO for homework 2
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
        PropertyManager manager = applicationTemplate.manager;

        ConfirmationDialog confirmationDialog = (ConfirmationDialog) applicationTemplate.getDialog(Dialog.DialogType.CONFIRMATION);
        confirmationDialog.show(manager.getPropertyValue(SAVE_UNSAVED_WORK_TITLE.name()), manager.getPropertyValue(SAVE_UNSAVED_WORK.name()));

        // analyze the dialog button clicked
        if (confirmationDialog.getSelectedOption() == Option.YES) {

            FileChooser fileChooser = new FileChooser();

            // create and add FileChooser ExtensionFilter for Tab-Separated Data Files (*.tsd)
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(manager.getPropertyValue(DATA_FILE_EXT_DESC.name()),
                    '*' + manager.getPropertyValue(DATA_FILE_EXT.name()));
            fileChooser.getExtensionFilters().add(extFilter);

            try {
                // only present Save As...Prompt if data has NOT been previously saved to a specified path
                if (dataFilePath == null) {
                    dataFilePath = fileChooser.showSaveDialog(applicationTemplate.getUIComponent().getPrimaryWindow()).toPath();
                }

                // create and write to new file if file is NOT null
                try (FileWriter fileWriter = new FileWriter(dataFilePath.toFile())) {
                    // write contents of textArea to file
                    fileWriter.write(((AppUI) applicationTemplate.getUIComponent()).getTextAreaData());
                } catch (Exception e) {
                    throw new IOException(manager.getPropertyValue(DATA_NOT_SAVED_WARNING.name()), e);
                }
            } catch (NullPointerException npe) {
                // saving was aborted by user
                throw new IOException(manager.getPropertyValue(DATA_NOT_SAVED_WARNING.name()), npe);
            }
        } else if (confirmationDialog.getSelectedOption() == Option.CANCEL) {
            // return false for CANCEL button click
            return false;
        }
        // return true for both YES and NO button clicks
        return true;
    }
}
