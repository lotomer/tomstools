package org.lotomer.plugins.tomstools.popup.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;


public abstract class TTBaseAction implements IObjectActionDelegate
{
    protected Shell shell;
    protected IStructuredSelection select;

    /**
     * Constructor for Action1.
     */
    public TTBaseAction()
    {
        super();
    }

    /**
     * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
     */
    public void setActivePart(IAction action, IWorkbenchPart targetPart)
    {
        shell = targetPart.getSite().getShell();
    }

    /**
     * @see IActionDelegate#selectionChanged(IAction, ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection)
    {
        if (selection instanceof IStructuredSelection)
        {
            select = (IStructuredSelection) selection;
        }
    }

}
