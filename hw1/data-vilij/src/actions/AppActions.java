// Aaron Knoll
package actions;

import algorithms.AlgorithmPauser;
import components.AlgConfigDialog;
import components.AlgorithmConfiguration;
import dataprocessors.AppData;
import java.io.File;
import static java.io.File.separator;
import java.io.FileWriter;
import vilij.components.ActionComponent;
import vilij.templates.ApplicationTemplate;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import static settings.AppPropertyTypes.*;
import ui.AppUI;
import ui.DataVisualizer;
import vilij.components.ConfirmationDialog;
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
    
    HashMap<String, Class> algorithmClasses;
    
    HashMap<String, AlgorithmConfiguration> classificationAlgConfigs;
    HashMap<String, AlgorithmConfiguration> clusteringAlgConfigs;

    /**
     * Indicates whether or not a new request is occurring for the first time
     * since application startup
     */
    private boolean firstNewRequest;

    public AppActions(ApplicationTemplate applicationTemplate) {
        this.applicationTemplate    = applicationTemplate;
        firstNewRequest             = true;
        classificationAlgConfigs    = new HashMap<>();
        clusteringAlgConfigs        = new HashMap<>();
        algorithmClasses            = new HashMap<>();
        clear();
    }

    @Override
    public void handleNewRequest() {
        // if it's NOT the first request to create new data since application 
        // startup...
        if (!firstNewRequest) {
            // try to prompt user to save current work before clearing and 
            // resetting data
            try {
                if (promptToSave()) {
                    applicationTemplate.getUIComponent().clear();
                    dataFilePath = null;
                }
            } catch (IOException promptException) {
                PropertyManager manager = applicationTemplate.manager;
                applicationTemplate.getDialog(Dialog.DialogType.ERROR)
                        .show(manager.getPropertyValue(DATA_NOT_SAVED_WARNING_TITLE.name()), promptException.getLocalizedMessage());
            }
        } else {
            firstNewRequest = false;
        }
        // show left column
        ((AppUI) (applicationTemplate.getUIComponent())).prepareUIForUserTypedInput();
    }

    @Override
    public void handleSaveRequest() {
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
            ((AppUI) applicationTemplate.getUIComponent()).newTextSaved();
            
        } catch (NullPointerException npe) {
            // do nothing. save was aborted by user.
        }
    }

    @Override
    public void handleLoadRequest() {
        PropertyManager manager = applicationTemplate.manager;
        FileChooser fileChooser = new FileChooser();

        // create and add FileChooser ExtensionFilter 
        // for Tab-Separated Data Files (*.tsd)
        FileChooser.ExtensionFilter extFilter = new FileChooser
                .ExtensionFilter(manager.getPropertyValue(DATA_FILE_EXT_DESC.name()),
                '*' + manager.getPropertyValue(DATA_FILE_EXT.name()));
        fileChooser.getExtensionFilters().add(extFilter);

        try {
            // present open file dialog
            dataFilePath = fileChooser.showOpenDialog(applicationTemplate.getUIComponent().getPrimaryWindow()).toPath();
            ((AppData) (applicationTemplate.getDataComponent())).loadData(dataFilePath);
        } catch (NullPointerException npe) {
            // do nothing. save was aborted by user.
        }
    }

    @Override
    public void handleExitRequest() {
        Thread t = ((AppUI) (applicationTemplate.getUIComponent())).getAlgThread();
        AlgorithmPauser p = ((AppUI) (applicationTemplate.getUIComponent())).getPauser();

        if (((AppUI) (applicationTemplate.getUIComponent())).hasUnsavedData()) {
            try {
                if (promptToSave()) {
                    if (t != null && t.isAlive()) {
                        promptToTerminateAlgExec(t, p);
                    } else {
                        Platform.exit();
                    }
                }
            } catch (IOException promptException) {
                PropertyManager manager = applicationTemplate.manager;
                applicationTemplate.getDialog(Dialog.DialogType.ERROR)
                        .show(manager.getPropertyValue(DATA_NOT_SAVED_WARNING_TITLE.name()), promptException.getLocalizedMessage());
            }
        } else if (t != null && t.isAlive()) {
            promptToTerminateAlgExec(t, p);
        } else {
            Platform.exit();
        }
    }

    @Override
    public void handlePrintRequest() {
        // N/A
    }

    /**
     * Prompts the user to save an image of the current state of the chart (in 
     * PNG format) at a specified location.
     *
     * @throws IOException
     */
    public void handleScreenshotRequest() throws IOException {
        
        PropertyManager manager = applicationTemplate.manager;
        
        WritableImage wi = ((AppUI) (applicationTemplate.getUIComponent())).getChart().snapshot(new SnapshotParameters(), null);
        
        FileChooser fileChooser = new FileChooser();
        // create and add FileChooser ExtensionFilter for PNG Files (*.png)
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(manager.getPropertyValue(IMAGE_FILE_EXT_DESC.name()),
                '*' + manager.getPropertyValue(IMAGE_FILE_EXT.name()));
        fileChooser.getExtensionFilters().add(extFilter);

        try {
            Path imageFilePath = fileChooser.showSaveDialog(applicationTemplate.getUIComponent().getPrimaryWindow()).toPath();

            try {
                ImageIO.write(SwingFXUtils.fromFXImage(wi, null), "png", imageFilePath.toFile());
            } catch (IOException ex) {
                Logger.getLogger(AppUI.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (NullPointerException npe) {
            // saving was aborted by user
        }
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
        
        PropertyManager manager = applicationTemplate.manager;

        ConfirmationDialog confirmationDialog = (ConfirmationDialog) applicationTemplate.getDialog(Dialog.DialogType.CONFIRMATION);
        confirmationDialog.show(manager.getPropertyValue(SAVE_UNSAVED_WORK_TITLE.name()), manager.getPropertyValue(SAVE_UNSAVED_WORK.name()));

        // analyze the dialog button clicked
        if (confirmationDialog.getSelectedOption() == ConfirmationDialog.Option.YES) {
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
                    ((AppUI) applicationTemplate.getUIComponent()).newTextSaved();
                } catch (Exception e) {
                    throw new IOException(manager.getPropertyValue(DATA_NOT_SAVED_WARNING.name()), e);
                }
            } catch (NullPointerException npe) {
                // saving was aborted by user
                throw new IOException(manager.getPropertyValue(DATA_NOT_SAVED_WARNING.name()), npe);
            }
        } else if (confirmationDialog.getSelectedOption() == ConfirmationDialog.Option.CANCEL) {
            // return false for CANCEL button click
            return false;
        }
        // return true for both YES and NO button clicks
        return true;
    }

    /**
     * Assigns runtime configuration settings to Algorithms.
     *
     * @param algName
     * @return true if the Algorithm configuration was modified, or false
     * otherwise
     */
    public boolean configAlgorithm(String algName) {

        AlgConfigDialog algConfigDialog = DataVisualizer.getAlgConfigDialog();
        
        if (classificationAlgConfigs.containsKey(algName)) {
            if (classificationAlgConfigs.get(algName) == null) {
                AlgorithmConfiguration defaultClassificationConfig = new AlgorithmConfiguration(1000, 5, true, false, 4);

                if (algConfigDialog.show(defaultClassificationConfig)) {
                    // save potentially modified configuration settings
                    classificationAlgConfigs.put(algName, defaultClassificationConfig);
                    return true;
                }
            } else {
                if (algConfigDialog.show(classificationAlgConfigs.get(algName))) {
                    // save potentially modified configuration settings
                    classificationAlgConfigs.put(algName, classificationAlgConfigs.get(algName));
                    return true;
                }
            }
        }

        if (clusteringAlgConfigs.containsKey(algName)) {
            if (clusteringAlgConfigs.get(algName) == null) {
                AlgorithmConfiguration defaultClusteringConfig = new AlgorithmConfiguration(1000, 5, true, true, 4);

                if (algConfigDialog.show(defaultClusteringConfig)) {
                    // save potentially modified configuration settings
                    clusteringAlgConfigs.put(algName, defaultClusteringConfig);
                    return true;
                }
            } else {
                if (algConfigDialog.show(clusteringAlgConfigs.get(algName))) {
                    // save potentially modified configuration settings
                    clusteringAlgConfigs.put(algName, clusteringAlgConfigs.get(algName));
                    return true;
                }
            }
        }

        return false;
    }
    
    /**
     * Getter for a particular Algorithm Class that was previously loaded 
     * dynamically using reflection.
     *
     * @param prettyName
     * @return Class for the specified Algorithm name.
     */
    public Class getAlgorithm(String prettyName) {
        
        return algorithmClasses.get(prettyName);
    }

    /**
     * Getter for Algorithm runtime configuration settings.
     *
     * @param index specifies the corresponding desired Algorithm type (0 for
     * classification, and 1 for clustering)
     * @return HashMap of configuration settings for available Algorithms of the
     * specified desired type
     */
    public HashMap<String, AlgorithmConfiguration> getAlgConfigs(int index) {
        if (index == 0) {
            return classificationAlgConfigs;
        }

        return clusteringAlgConfigs;
    }
    
    /**
     * Clears and loads HashMaps for tracking dynamically loaded Algorithms 
     * and their runtime configuration settings. Algorithms to be loaded at 
     * runtime must be located in appropriate folders (labeled by Algorithm 
     * type), in order to be read by the application. 
     */
    public void clear() {
        algorithmClasses.clear();
        classificationAlgConfigs.clear();
        clusteringAlgConfigs.clear();

        ClassLoader classLoader = AppActions.class.getClassLoader();
        File[] classifierClasses = (new File(String.join(separator,
                System.getProperty("user.dir"),
                "data-vilij",
                "src",
                "classification"))).listFiles();

        try {
            for (File f : classifierClasses) {
                Class c = classLoader.loadClass("classification."
                        + f.getName().substring(0, f.getName().lastIndexOf(".java")));

                try {
                    try {
                        algorithmClasses.put(c.getMethod("getPrettyName").invoke(null).toString(), c);
                        classificationAlgConfigs.put(c.getMethod("getPrettyName").invoke(null).toString(), null);
                    } catch (NoSuchMethodException | SecurityException ex) {
                        Logger.getLogger(AppActions.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException ex) {
                    Logger.getLogger(AppActions.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AppActions.class.getName()).log(Level.SEVERE, null, ex);
        }

        File[] clustererClasses = (new File(String.join(separator,
                System.getProperty("user.dir"),
                "data-vilij",
                "src",
                "clustering"))).listFiles();

        try {
            for (File f : clustererClasses) {
                Class c = classLoader.loadClass("clustering."
                        + f.getName().substring(0, f.getName().lastIndexOf(".java")));
                
                try {
                    try {
                        algorithmClasses.put(c.getMethod("getPrettyName").invoke(null).toString(), c);
                        clusteringAlgConfigs.put(c.getMethod("getPrettyName").invoke(null).toString(), null);
                    } catch (NoSuchMethodException | SecurityException ex) {
                        Logger.getLogger(AppActions.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException ex) {
                    Logger.getLogger(AppActions.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AppActions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Prompts the user to terminate a running Algorithm.
     *
     * @param t Algorithm thread.
     * @param p AlgorithmPauser instance.
     */
    private void promptToTerminateAlgExec(Thread t, AlgorithmPauser p) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Algorithm Execution in Progress");
        alert.setHeaderText("An Algorithm is Running!");
        alert.setContentText("Are you sure you want to terminate it?");

        ButtonType yesBtn = new ButtonType("Yes");
        ButtonType noBtn = new ButtonType("No");

        alert.getButtonTypes().setAll(yesBtn, noBtn);

        if (alert.showAndWait().get() == yesBtn) {
            t.interrupt();
            p.terminateRunningAlgThread();

            Platform.exit();
        }
    }
}
