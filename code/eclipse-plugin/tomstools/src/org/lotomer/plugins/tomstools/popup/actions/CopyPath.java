package org.lotomer.plugins.tomstools.popup.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IActionDelegate;
import org.lotomer.plugins.tomstools.Activator;
import org.lotomer.plugins.tomstools.common.Message;
import org.lotomer.plugins.tomstools.common.PluginUtils;
import org.lotomer.plugins.tomstools.preferences.PreferenceConstants;


public class CopyPath extends TTBaseAction
{    
    /**
     * @see IActionDelegate#run(IAction)
     */
    public void run(IAction action)
    {
        // 根据配置截取掉指定前缀
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        String prefix = store.getString(PreferenceConstants.P_COPY_PATH_PREFIX);
        Object[] objects = select.toArray();

        String paths = "";
        String path = "";
        for (Object obj : objects)
        {
            path = PluginUtils.getAbsolutePath(obj);
            if (!PluginUtils.isEmpty(prefix))
            {
                int index = path.toLowerCase().indexOf(prefix.toLowerCase());
                if (-1 < index)
                {
                    path = path.substring(prefix.length());
                }
            }
            paths = paths + path + "\n";
        }

        if (PluginUtils.isEmpty(paths))
        {
            return;
        }

        // 将路径复制到剪贴板
        PluginUtils.copy2clipboard(shell.getDisplay(), paths);
        
        MessageDialog.openInformation(shell, Message.getString("plugin", "TomsTool"), Message
                .getString("Copy2ClipboardSuccess")
                + "\n" + paths);
    }
}
