package org.lotomer.plugins.tomstools.popup.actions;

import java.io.File;
import java.io.IOException;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IActionDelegate;
import org.lotomer.plugins.tomstools.Activator;
import org.lotomer.plugins.tomstools.common.PluginUtils;
import org.lotomer.plugins.tomstools.preferences.PreferenceConstants;


public class OpenInExplorer extends TTBaseAction
{
    /**
     * @see IActionDelegate#run(IAction)
     */
    public void run(IAction action)
    {
        // 获取配置项的值
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        Object obj = select.getFirstElement();
        String path = PluginUtils.getAbsolutePath(obj);
        execute(store, path);
    }

    /**
     * 在资源管理器中打开文件所在目录
     * @param store
     * @param path 文件路径
     */
    public static void execute(IPreferenceStore store, String path) {
        String command4dir = store.getString(PreferenceConstants.P_OPEN_IN_EXPLORER_COMMAND_FOR_DIR);
        String command4file = store.getString(PreferenceConstants.P_OPEN_IN_EXPLORER_COMMAND_FOR_FILE);
        String command = "";
        File file = new File(path);
        if (file != null && file.isDirectory())
        {
            command = command4dir;
        }
        else
        {
            command = command4file;
        }
        // 替换其中的%1为真实的路径
        if (!PluginUtils.isEmpty(command))
        {
            command = command.replace("%1", path);
        }
        System.out.println(command);
        try
        {
            Runtime runtime = Runtime.getRuntime();
            // 调用外部命令定位文件或目录
            runtime.exec(command);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
