// Aaron Knoll
package actions;

import algorithms.Algorithm;
import components.AlgConfigDialog;
import components.AlgorithmConfiguration;
import data.DataSet;
import dataprocessors.AppData;
import java.io.File;
import static java.io.File.separator;
import java.io.FileWriter;
import vilij.components.ActionComponent;
import vilij.templates.ApplicationTemplate;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.stage.FileChooser;
import static settings.AppPropertyTypes.*;
import ui.AlgResourcePreparer;
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
                if (!firstNewRequest && promptToSave()) {
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
    
    public Class getAlgorithm(String prettyName) {
        
        return algorithmClasses.get(prettyName);
    }

    public HashMap<String, AlgorithmConfiguration> getAlgConfigs(int index) {
        if (index == 0) {
            return classificationAlgConfigs;
        }

        return clusteringAlgConfigs;
    }
    
    public void clear() {
        classificationAlgConfigs.clear();
        clusteringAlgConfigs.clear();

        ClassLoader classLoader = AppActions.class.getClassLoader();
        File[] classifierClasses = (new File(String.join(separator,
                System.getProperty("user.dir"),
                "data-vilij",
                "src",
                "classification"))).listFiles();

//        if (classifierClasses != null) {
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

//                System.out.println("aClass.getName() = " + c.getName() + " Methods: " + Arrays.toString(c.getDeclaredMethods()));

            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AppActions.class.getName()).log(Level.SEVERE, null, ex);
        }
//        } else {
//            System.out.println("Boom!");
//        }
//        System.out.println();

        File[] clustererClasses = (new File(String.join(separator,
                System.getProperty("user.dir"),
                "data-vilij",
                "src",
                "clustering"))).listFiles();

//        if (clustererClasses != null) {
        try {
            for (File f : clustererClasses) {
                Class c = classLoader.loadClass("clustering."
                        + f.getName().substring(0, f.getName().lastIndexOf(".java")));
                
//                c.getConstructor(DataSet.class,
//                        int.class,
//                        int.class,
//                        int.class,
//                        boolean.class,
//                        AlgResourcePreparer.class);
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

//                System.out.println("aClass.getName() = " + c.getName() + " Methods: " + Arrays.toString(c.getDeclaredMethods()));

            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AppActions.class.getName()).log(Level.SEVERE, null, ex);
        }
//        } else {
//            System.out.println("Boom!");
//        }
    }
}
