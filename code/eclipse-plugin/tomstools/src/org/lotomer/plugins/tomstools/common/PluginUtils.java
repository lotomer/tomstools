package org.lotomer.plugins.tomstools.common;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.jsdt.core.IJavaScriptElement;

public class PluginUtils
{
    // 获取对象的绝对路径
    @SuppressWarnings("restriction")
    public static String getAbsolutePath(Object obj)
    {
        String path = "";
        if (obj instanceof IResource)
        {
            IResource file = (IResource) obj; // 文件和目录
            path = file.getLocation().toOSString();
        }
        else if (obj instanceof FileEditorInput)
        {
            FileEditorInput edit = (FileEditorInput) obj; // 编辑器
            path = edit.getPath().toOSString();
        }
        else if (obj instanceof IJavaElement)
        {
            IJavaElement element = (IJavaElement) obj; // 包及类文件
            path = element.getResource().getLocation().toOSString();
        }     
        else if (obj instanceof IJavaScriptElement)
        {
            IJavaScriptElement element = (IJavaScriptElement) obj;
            path = element.getResource().getLocation().toOSString();
        }
        else if (obj instanceof org.eclipse.wst.server.core.internal.Server)
        {
            org.eclipse.wst.server.core.internal.Server element = (org.eclipse.wst.server.core.internal.Server) obj;
            path = element.getRuntime().getLocation().toOSString();
        }
        else
        {
            // 输出对象名（我就是利用他把上面的一个一个试出来的）
            path = obj.getClass().toString();
        }
        
        return path;
    }

    /**
     * 判断是否为空。
     * 
     * @param value
     *            待校验的字符串
     * @return 为null或空串则返回true，否则返回false
     */
    public static boolean isEmpty(final String value)
    {
        return (null == value) || ("".equals(value));
    }

    /**
     * 将指定文本复制到剪贴板中
     * @param display
     * @param value   指定文本
     */
    public static void copy2clipboard(Display display, String value)
    {
        Clipboard cb = new Clipboard(display);
        TextTransfer transfer = TextTransfer.getInstance();
        cb.setContents(new Object[] { value }, new TextTransfer[] { transfer });
        cb.dispose();
    }
}
