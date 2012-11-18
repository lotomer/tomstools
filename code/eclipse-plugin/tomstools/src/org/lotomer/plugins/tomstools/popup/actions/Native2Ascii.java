package org.lotomer.plugins.tomstools.popup.actions;

import java.io.IOException;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IActionDelegate;
import org.lotomer.plugins.tomstools.Activator;
import org.lotomer.plugins.tomstools.common.Message;
import org.lotomer.plugins.tomstools.common.PluginUtils;
import org.lotomer.plugins.tomstools.preferences.PreferenceConstants;


public class Native2Ascii extends TTBaseAction
{    
    /**
     * @see IActionDelegate#run(IAction)
     */
    public void run(IAction action)
    {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        Object obj = select.getFirstElement();
        String path = PluginUtils.getAbsolutePath(obj);
        String nativeFile = "";
        int index = path.lastIndexOf(".");
        if (-1 < index)
        {
            nativeFile = path.substring(0,index);
            nativeFile += "_" + store.getString(PreferenceConstants.P_NATIVE_FILE_POSTFIX) + path.substring(index);
        }
        else
        {
            nativeFile = path + "_" + store.getString(PreferenceConstants.P_NATIVE_FILE_POSTFIX);
        }
        
        String command = store.getString(PreferenceConstants.P_NATIVE_COMMAND);
        command = command.replace("%1", path).replace("%2", nativeFile);
        try
        {
            Runtime.getRuntime().exec(command);
            MessageDialog.openInformation(shell, "", Message.getString("native2ascii.success") + "\n" + command);
        }
        catch (IOException e)
        {
            MessageDialog.openError(shell, "", e.getMessage());
        }        
    }
}
