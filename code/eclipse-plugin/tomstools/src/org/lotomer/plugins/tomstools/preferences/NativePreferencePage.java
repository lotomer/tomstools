package org.lotomer.plugins.tomstools.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.lotomer.plugins.tomstools.Activator;
import org.lotomer.plugins.tomstools.common.Message;


/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class NativePreferencePage extends FieldEditorPreferencePage implements
        IWorkbenchPreferencePage
{

    private StringFieldEditor edtNativeCommand;
    private StringFieldEditor edtFilePostfix;
    private StringFieldEditor edtFileSample;
    private StringFieldEditor edtFileSampleFilled;

    public NativePreferencePage()
    {
        super(GRID);
        setPreferenceStore(Activator.getDefault().getPreferenceStore());
        setDescription(Message.getString("native2ascii.description"));
    }

    /**
     * Creates the field editors. Field editors are abstractions of the common
     * GUI blocks needed to manipulate various types of preferences. Each field
     * editor knows how to save and restore itself.
     */
    public void createFieldEditors()
    {
        edtNativeCommand = new StringFieldEditor(PreferenceConstants.P_NATIVE_COMMAND, Message.getString("native2ascii.command"),
                getFieldEditorParent());
        addField(edtNativeCommand);
        
        edtFilePostfix = new StringFieldEditor(PreferenceConstants.P_NATIVE_FILE_POSTFIX, Message.getString("native2ascii.file.postfix"),
                getFieldEditorParent());
        addField(edtFilePostfix);
        
        edtFileSample = new StringFieldEditor(PreferenceConstants.P_NATIVE_FILE_SAMPLE, Message.getString("native2ascii.file.sample"),
                getFieldEditorParent()); 
        addField(edtFileSample);
        edtFileSample.getTextControl(getFieldEditorParent()).setEnabled(false);
        
        edtFileSampleFilled = new StringFieldEditor(PreferenceConstants.P_NATIVE_FILE_SAMPLE_FILLED, Message.getString("native2ascii.file.sample.filled"),
                getFieldEditorParent());
        addField(edtFileSampleFilled);
        
        edtFileSampleFilled.getTextControl(getFieldEditorParent()).setEnabled(false);
        
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.FieldEditorPreferencePage#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
     */
    @Override
    public void propertyChange(PropertyChangeEvent event)
    {
        Object obj = event.getSource();
        if (obj instanceof StringFieldEditor)
        {
            StringFieldEditor editor = (StringFieldEditor) obj;
            if (PreferenceConstants.P_NATIVE_FILE_POSTFIX.equals(editor.getPreferenceName()))
            {
                doChange(edtFilePostfix.getStringValue(),edtFileSample.getStringValue());
            }            
        }
        
        super.propertyChange(event);
    }

    private void doChange(String postfix, String sample)
    {
        if (null == sample || "".equals(sample))
        {
            edtFileSampleFilled.setStringValue(postfix);
            return;
        }
        if (null == postfix || "".equals(postfix))
        {
            edtFileSampleFilled.setStringValue(sample);
            return;
        }
        int index = sample.lastIndexOf(".");
        if (-1 < index)
        {
            edtFileSampleFilled.setStringValue(sample.substring(0,index) + "_" + postfix + sample.substring(index));            
        }
        else
        {
            edtFileSampleFilled.setStringValue(sample);
        }        
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    public void init(IWorkbench workbench)
    {
    }

}