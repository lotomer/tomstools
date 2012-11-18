package org.lotomer.plugins.tomstools.popup.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IActionDelegate;
import org.lotomer.utils.classsearch.shell.UIClassSearcher;

public class ClassSearch extends TTBaseAction
{    
    /**
     * @see IActionDelegate#run(IAction)
     */
    public void run(IAction action)
    {        
        UIClassSearcher.show(shell.getDisplay());
    }
}
