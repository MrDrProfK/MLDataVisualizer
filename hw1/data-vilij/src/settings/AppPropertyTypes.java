package settings;

/**
 * This enumerable type lists the various application-specific property types listed in the initial set of properties to
 * be loaded from the workspace properties <code>xml</code> file specified by the initialization parameters.
 *
 * @author Ritwik Banerjee
 * @see vilij.settings.InitializationParams
 */
public enum AppPropertyTypes {

    /* resource files and folders */
    DATA_RESOURCE_PATH,

    /* user interface icon file names */
    SCREENSHOT_ICON,
    
    /* user interface object label text */
    DATA_FILE_LABEL_TEXT,
    DISPLAY_BUTTON_TEXT,
    GRAPH_LABEL_TEXT,
    
    /* tooltips for user interface buttons */
    SCREENSHOT_TOOLTIP,

    /* warning messages */
    EXIT_WHILE_RUNNING_WARNING,
    DATA_NOT_SAVED_WARNING,
    
    /* error messages */
    RESOURCE_SUBDIR_NOT_FOUND,
    INVALID_DATA_FORMAT,

    /* application-specific message titles */
    SAVE_UNSAVED_WORK_TITLE,
    DATA_NOT_SAVED_WARNING_TITLE,

    /* application-specific messages */
    SAVE_UNSAVED_WORK,

    /* application-specific parameters */
    DATA_FILE_EXT,
    DATA_FILE_EXT_DESC,
    TEXT_AREA,
    SPECIFIED_FILE
}
