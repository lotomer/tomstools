package org.lotomer.plugins.tomstools.popup.actions;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class GetClassInfo implements IObjectActionDelegate {
    protected Shell shell;
    protected IStructuredSelection select;

    public GetClassInfo() {
        super();
    }

    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        shell = targetPart.getSite().getShell();
    }

    public void selectionChanged(IAction action, ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            select = (IStructuredSelection) selection;
        }
    }

    public void run(IAction arg0) {
        if (select instanceof IJavaElement) {
            // IJavaElement javaElement = (IJavaElement) select;
            // Class<? extends IJavaElement> javaClazz = javaElement.getClass();
            // // 获取类名
            // String clazzName = javaClazz.getName();
            // // 获取方法列表
            // Method[] methods = javaClazz.getMethods();
            // // 获取属性列表
            // Field[] fields = javaClazz.getDeclaredFields();
            // // 获取路径
            // String path = javaElement.getResource().getLocation().toOSString();
        }

    }

}
