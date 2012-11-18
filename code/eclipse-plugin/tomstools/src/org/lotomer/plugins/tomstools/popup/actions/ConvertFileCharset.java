package org.lotomer.plugins.tomstools.popup.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.lotomer.plugins.tomstools.Activator;
import org.lotomer.plugins.tomstools.common.FileUtil;
import org.lotomer.plugins.tomstools.common.Message;
import org.lotomer.plugins.tomstools.common.PluginUtils;
import org.lotomer.plugins.tomstools.preferences.PreferenceConstants;

public class ConvertFileCharset extends TTBaseAction {
    public void run(IAction action) {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        // 获取配置的文件字符集
        String srcCharsetName = store
                .getString(PreferenceConstants.P_CONVERT_FILE_CHARSET_SRC_CHARSET);
        String destCharsetName = store
                .getString(PreferenceConstants.P_CONVERT_FILE_CHARSET_DEST_CHARSET);
        String srcPath = store.getString(PreferenceConstants.P_CONVERT_FILE_CHARSET_SRC_PATH);
        String destPath = store.getString(PreferenceConstants.P_CONVERT_FILE_CHARSET_DEST_PATH);

        if (MessageDialog.openConfirm(shell, Message.getString("plugin", "TomsTool"), String
                .format(Message.getString("ConfirmConvertFileCharset.charset"), srcCharsetName,
                        destCharsetName))) {
            if (!PluginUtils.isEmpty(srcPath) && !PluginUtils.isEmpty(destPath)) {
                // 直接转换指定目录的文件
                if (MessageDialog.openConfirm(shell, Message.getString("plugin", "TomsTool"),
                        String.format(Message.getString("ConfirmConvertFileCharset.path"), srcPath,
                                destPath))) {
                    FileUtil.convertFileCharset(srcPath, destPath, srcCharsetName, destCharsetName,
                            true);
                    MessageDialog.openInformation(shell, Message.getString("plugin", "TomsTool"), Message
                            .getString("ConvertSuccess"));
                    return;
                }
            }

            Object[] objects = select.toArray();
            String path = "";
            for (Object obj : objects) {
                path = PluginUtils.getAbsolutePath(obj);
                if (!PluginUtils.isEmpty(path)) {
                    FileUtil.convertFileCharset(path, path, srcCharsetName, destCharsetName, true);
                }
            }

            MessageDialog.openInformation(shell, Message.getString("plugin", "TomsTool"), Message
                    .getString("ConvertSuccess"));
        }
    }
}
