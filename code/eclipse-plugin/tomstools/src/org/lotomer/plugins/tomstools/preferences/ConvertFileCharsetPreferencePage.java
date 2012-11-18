/**
 * Copyright @ 2009-2012 tomstools.org
 */
package org.lotomer.plugins.tomstools.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.lotomer.plugins.tomstools.Activator;
import org.lotomer.plugins.tomstools.common.Message;

/**
 * 文件字符集转换配置界面
 * 
 * @author lotomer
 * @create-time 2012-11-17 下午8:27:57
 */
public class ConvertFileCharsetPreferencePage extends FieldEditorPreferencePage implements
        IWorkbenchPreferencePage {

    public void init(IWorkbench arg0) {
        setPreferenceStore(Activator.getDefault().getPreferenceStore());
        setDescription(Message.getString("convertFileCharset.description"));
    }

    @Override
    protected void createFieldEditors() {
        addField(new StringFieldEditor(PreferenceConstants.P_CONVERT_FILE_CHARSET_SRC_CHARSET, Message.getString("convertFileCharset.srcCharset"),
                getFieldEditorParent()));
        addField(new StringFieldEditor(PreferenceConstants.P_CONVERT_FILE_CHARSET_DEST_CHARSET, Message.getString("convertFileCharset.destCharset"),
                getFieldEditorParent()));
        addField(new StringFieldEditor(PreferenceConstants.P_CONVERT_FILE_CHARSET_SRC_PATH, Message.getString("convertFileCharset.srcPath"),
                getFieldEditorParent()));
        addField(new StringFieldEditor(PreferenceConstants.P_CONVERT_FILE_CHARSET_DEST_PATH, Message.getString("convertFileCharset.destPath"),
                getFieldEditorParent()));
    }

}
