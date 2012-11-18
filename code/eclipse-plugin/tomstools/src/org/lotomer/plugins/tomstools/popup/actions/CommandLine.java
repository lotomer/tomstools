package org.lotomer.plugins.tomstools.popup.actions;

import java.io.File;
import java.io.IOException;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.lotomer.plugins.tomstools.Activator;
import org.lotomer.plugins.tomstools.common.PluginUtils;
import org.lotomer.plugins.tomstools.preferences.PreferenceConstants;


public class CommandLine extends TTBaseAction
{    
    public void run(IAction arg0)
    {
        //获取选中的第一个
        Object obj = select.getFirstElement();
        
        //获取选中文件的路径
        String path = PluginUtils.getAbsolutePath(obj);
        
        //获取目录名。如果是文件，则取其所在目录名；如果是目录，则直接取其名
        File file = new File(path);        
        String directPath = file.getAbsolutePath();
        if (file.isFile())
        {
            directPath = file.getParent();
        }
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        String command = store.getString(PreferenceConstants.P_COMMAND_LINE_COMMAND);
        if (PluginUtils.isEmpty(command))
        {
            return;
        }
        command = command.replace("%1", directPath);
        try
        {
            //执行外部命令
            Runtime.getRuntime().exec(command);           
        }
        catch (IOException e)
        {
            MessageDialog.openError(shell, "", e.getMessage());            
        }                
    }
}
