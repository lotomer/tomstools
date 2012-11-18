package org.lotomer.plugins.tomstools.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.lotomer.plugins.tomstools.Activator;


/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer
{
    public void initializeDefaultPreferences()
    {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();

        store.setDefault(PreferenceConstants.P_NATIVE_COMMAND, PreferenceConstants.P_NATIVE_COMMAND_DEFAULT);
        store.setDefault(PreferenceConstants.P_NATIVE_FILE_POSTFIX, PreferenceConstants.P_NATIVE_FILE_POSTFIX_DEFAULT);
        store.setDefault(PreferenceConstants.P_NATIVE_FILE_SAMPLE, PreferenceConstants.P_NATIVE_FILE_SAMPLE_DEFAULT);
        store.setDefault(PreferenceConstants.P_NATIVE_FILE_SAMPLE_FILLED, PreferenceConstants.P_NATIVE_FILE_SAMPLE_FILLED_DEFAULT);
        store.setDefault(PreferenceConstants.P_COMMAND_LINE_COMMAND, PreferenceConstants.P_COMMAND_LINE_COMMAND_DEFAULT);

        store.setDefault(PreferenceConstants.P_OPEN_IN_EXPLORER_COMMAND_FOR_DIR, PreferenceConstants.P_OPEN_IN_EXPLORER_COMMAND_FOR_DIR_DEFAULT);
        store.setDefault(PreferenceConstants.P_OPEN_IN_EXPLORER_COMMAND_FOR_FILE, PreferenceConstants.P_OPEN_IN_EXPLORER_COMMAND_FOR_FILE_DEFAULT);

        store.setDefault(PreferenceConstants.P_CONVERT_FILE_CHARSET_SRC_CHARSET, PreferenceConstants.P_CONVERT_FILE_CHARSET_SRC_CHARSET_DEFAULT);
        store.setDefault(PreferenceConstants.P_CONVERT_FILE_CHARSET_DEST_CHARSET, PreferenceConstants.P_CONVERT_FILE_CHARSET_DEST_CHARSET_DEFAULT);
        store.setDefault(PreferenceConstants.P_CONVERT_FILE_CHARSET_SRC_PATH, PreferenceConstants.P_CONVERT_FILE_CHARSET_SRC_PATH_DEFAULT);
        store.setDefault(PreferenceConstants.P_CONVERT_FILE_CHARSET_DEST_PATH, PreferenceConstants.P_CONVERT_FILE_CHARSET_DEST_PATH_DEFAULT);
    }

}
