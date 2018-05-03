// Aaron Knoll
package dataprocessors;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import ui.AppUI;
import vilij.components.DataComponent;
import vilij.templates.ApplicationTemplate;

import java.nio.file.Path;
import java.util.ArrayList;
import static settings.AppPropertyTypes.*;
import vilij.components.Dialog;
import vilij.propertymanager.PropertyManager;
import static vilij.settings.PropertyTypes.*;

/**
 * This is the concrete application-specific implementation of the data
 * component defined by the Vilij framework.
 *
 * @author Ritwik Banerjee
 * @see DataComponent
 */
public class AppData implements DataComponent {

    private TSDProcessor processor;
    private ApplicationTemplate applicationTemplate;

    public AppData(ApplicationTemplate applicationTemplate) {
        this.processor = new TSDProcessor();
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    public void loadData(Path dataFilePath) {
        // TODO for homework 2
        PropertyManager manager = applicationTemplate.manager;
        // clear data from data processor
        clear();
        try{
            // load data from file if file is NOT null
            FileReader fileReader = new FileReader(dataFilePath.toFile());
            try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                // read contents of loaded file line by line and store them in an ArrayList
                String singleLineInput, strToBeProcessed = "";
                ArrayList<String> dataByLine = new ArrayList<>();

                while ((singleLineInput = bufferedReader.readLine()) != null) {
                    dataByLine.add(singleLineInput);
                    strToBeProcessed += singleLineInput + "\n";
                }

                ((AppUI) applicationTemplate.getUIComponent()).prepareUIForFileLoadedInput(dataByLine, processor.getErrorLineNumber(strToBeProcessed), dataFilePath);
                // TODO: replace hard-coded strings
                if (dataByLine.size() > 10) {
                    applicationTemplate.getDialog(Dialog.DialogType.ERROR)
                            .show("Data Loaded Successfully",
                                    "Loaded data consists of "
                                    + dataByLine.size()
                                    + " line(s). Showing the first 10 in the text area.");
                } else {
                    applicationTemplate.getDialog(Dialog.DialogType.ERROR)
                            .show("Data Loaded Successfully",
                                    "Loaded data consists of "
                                    + dataByLine.size()
                                    + " line(s).");
                }

            }
        } catch (Exception ex) {
            // TODO: create appropriate Dialog Box
            applicationTemplate.getDialog(Dialog.DialogType.ERROR)
                    .show(manager.getPropertyValue(LOAD_ERROR_TITLE.name()),
                            ex.getMessage());
        }
    }

    public boolean loadData(String dataString) {
        // TODO for homework 1
        PropertyManager manager = applicationTemplate.manager;
        clear();
        try {
            if (processor.getErrorLineNumber(dataString) != null) {
                // plot data
//                displayData();
            }
        } catch (Exception ex) {
            applicationTemplate.getDialog(Dialog.DialogType.ERROR)
                    .show(manager.getPropertyValue(LOAD_ERROR_TITLE.name()),
                            ex.getMessage());
            return false;
        }
        
        // if data was successfully loaded
        return true;
    }

    @Override
    public void saveData(Path dataFilePath) {
        // TODO for homework 2
        PropertyManager manager = applicationTemplate.manager;
        String strToBeProcessed = ((AppUI) applicationTemplate.getUIComponent()).getTextAreaData();
        try {
            if (processor.getErrorLineNumber(strToBeProcessed) != null) {
                // create and write to new file if file is NOT null
                try (FileWriter fileWriter = new FileWriter(dataFilePath.toFile())) {
                    // write contents of textArea to file
                    fileWriter.write(strToBeProcessed);
                    ((AppUI) applicationTemplate.getUIComponent()).disableSaveButton();
                } catch (IOException promptException) {
                    // catch exception if write operation doesn't work as expected
                    applicationTemplate.getDialog(Dialog.DialogType.ERROR)
                            .show(manager.getPropertyValue(DATA_NOT_SAVED_WARNING_TITLE.name()), manager.getPropertyValue(DATA_NOT_SAVED_WARNING.name()));
                }
            }
        } catch (Exception ex) {
            applicationTemplate.getDialog(Dialog.DialogType.ERROR)
                    .show(manager.getPropertyValue(DATA_NOT_SAVED_WARNING_TITLE.name()),
                            ex.getMessage());
        }
        // clear data from data processor
        clear();
    }

    @Override
    public void clear() {
        processor.clear();
    }

    public void displayData() {
        processor.toChartData(((AppUI) applicationTemplate.getUIComponent()).getChart());
        processor.displayInstanceNamesWhenHovering(((AppUI) applicationTemplate.getUIComponent()).getChart());
    }
}
