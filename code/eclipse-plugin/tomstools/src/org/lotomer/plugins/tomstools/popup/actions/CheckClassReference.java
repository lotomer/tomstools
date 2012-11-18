package org.lotomer.plugins.tomstools.popup.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.lotomer.plugins.tomstools.common.Message;
import org.lotomer.plugins.tomstools.common.PluginUtils;

public class CheckClassReference extends TTBaseAction
{
    //private Logger logger = Logger.getLogger(CheckClassReference.class.getName());
    
    public void run(IAction arg0)
    {
        Object[] objects = select.toArray();
        
        for (Object obj : objects)
        {
            if (obj instanceof IJavaProject)
            {
                IJavaProject javaProject = (IJavaProject) obj;
                List<JavaFile> files = new ArrayList<JavaFile>();
                try
                {
                    IPackageFragmentRoot[] childs = javaProject.getPackageFragmentRoots();
                    for (IPackageFragmentRoot javaElement : childs)
                    {
                        if (IPackageFragmentRoot.K_SOURCE == javaElement.getKind())
                        {
                            // 检查前准备，获取工程下所有java类信息
                            checkPrepare(files, javaElement);
                        }
                    }
                    
                    // 开始检查
                    doCheck(files);
                    
                    // 输出结果
                    print(files);
                }
                catch (JavaModelException e)
                {
                    e.printStackTrace();
                }
            }
            else if (obj instanceof IProject)
            {
                IProject project = (IProject) obj;
                IPath p = project.getFullPath();
                System.out.println(p.toOSString());
            }
        }
        
        // MessageDialog.openInformation(shell, Message.getString("plugin",
        // "TomsTool"), Message
        // .getString("Copy2ClipboardSuccess")
        // + "\n" + paths);
    }
    
    private void print(List<JavaFile> files)
    {
        StringBuffer clazzNames = new StringBuffer();
        int count = 0;
        for (JavaFile file : files)
        {
            //logger.warning(file.getPackageName() + "." + file.getClassName() + " refed by " + file.getRefs().size() + " classes.");
            if (0 == file.getRefs().size())
            {
                clazzNames.append(file.getPackageName());
                clazzNames.append(".");
                clazzNames.append(file.getClassName());
                clazzNames.append("\n");
                ++count;
            }
        }
        
        // 将结果复制到剪贴板
        PluginUtils.copy2clipboard(shell.getDisplay(), clazzNames.toString());
        
        MessageDialog.openInformation(shell, Message.getString("plugin", "TomsTool"), Message.getString("Copy2ClipboardSuccess") + "\n" + count);
    }
    
    /** 执行检查 */
    private void doCheck(List<JavaFile> files)
    {
        for (JavaFile file : files)
        {
            for (JavaFile file0 : files)
            {
                if (file0.ref(file))
                {
                    file.addRef(file0);
                }
            }
        }
    }
    
    private void checkPrepare(List<JavaFile> files, IJavaElement javaElement) throws JavaModelException
    {
        if (IJavaElement.PACKAGE_FRAGMENT_ROOT == javaElement.getElementType())
        {
            IPackageFragmentRoot p = (IPackageFragmentRoot) javaElement;
            
            IJavaElement[] childs = p.getChildren();
            for (IJavaElement e : childs)
            {
                checkPrepare(files, e);
            }
        }
        else if (IJavaElement.PACKAGE_FRAGMENT == javaElement.getElementType())
        {
            IPackageFragment p = (IPackageFragment) javaElement;
            IJavaElement[] childs = p.getChildren();
            for (IJavaElement e : childs)
            {
                checkPrepare(files, e);
            }
        }
        else if (IJavaElement.COMPILATION_UNIT == javaElement.getElementType())
        {
            // java文件
            ICompilationUnit cu = (ICompilationUnit) javaElement;
            IType[] types = cu.getAllTypes();
            for (IType type : types)
            {
                // System.out.println(cu.getElementName() + ":" +
                // type.getElementName() + ":" +
                // type.getPackageFragment().getElementName());
                
                JavaFile file = new JavaFile();
                file.setClassName(type.getElementName());
                file.setCode(cu.getSource());
                file.setPackageName(type.getPackageFragment().getElementName());
                
                IImportDeclaration[] imports = cu.getImports();
                for (IImportDeclaration aImport : imports)
                {
                    file.addImport(aImport.getElementName());
                }
                files.add(file);
            }
        }
        else
        {
            // System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
        }
    }
    
    private class JavaFile
    {
        private String         packageName;
        private String         className;
        private String         code;
        private List<JavaFile> refs    = new ArrayList<JavaFile>();
        private List<String>   imports = new ArrayList<String>();
        
        /**
         * 是否引用了指定文件
         * 
         * @param file
         *            可能被引用的指定文件
         * @return true 引用了指定文件；false 没有引用指定文件
         */
        public boolean ref(JavaFile file)
        {
            if (this == file) // 同一个文件
            {
                return false;
            }
            
            // 是否是同一包下的文件
            String srcPkgName = file.getPackageName();
            String srcClassName = file.getClassName();
            //System.out.println(srcPkgName + "." + srcClassName  + " => " + this.packageName + "." + this.className);
            if (this.packageName.equals(srcPkgName))
            {
                // 同一包下的类
                return containClass(code, srcClassName);
            }
            else
            {
                int index = code.indexOf(srcPkgName + "." + srcClassName);
                if (-1 < index) // 完全匹配
                {
                    return true;
                }
                index = code.indexOf(srcPkgName + ".*");
                if (-1 < index)
                {
                    return containClass(code.substring(index + (srcPkgName + ".*").length()), srcClassName);
                }
                else
                {
                    return false;
                }
            }
        }
        
        private boolean containClass(String code, String clazzName)
        {
            // 查看后面的代码中时候有类名
            int classIndex = code.indexOf(clazzName);
            if (-1 < classIndex) // 匹配上类名
            {
                // 检查类名前后时候为空白字符
                String prechar = code.substring(classIndex - 1, classIndex);
                String tailchar = code.substring(classIndex + clazzName.length(), classIndex + clazzName.length() + 1);
                if (("\t".equals(prechar) || " ".equals(prechar) | "(".equals(prechar))
                        && ("\t".equals(tailchar) || " ".equals(tailchar) || "(".equals(tailchar) || ".".equals(tailchar) || "{".equals(tailchar)))
                {
                    return true;
                }
                else
                {
                    return containClass(code.substring(classIndex + clazzName.length()), clazzName);
                }
            }
            else
            {
                return false;
            }
        }
        
        /**
         * @param imports
         *            the imports to set
         */
        public final void addImport(String aImport)
        {
            this.imports.add(aImport);
        }
        
        /**
         * @return the refs
         */
        public final List<JavaFile> getRefs()
        {
            return refs;
        }
        
        /**
         * @param refs
         *            the refs to set
         */
        public final void addRef(JavaFile ref)
        {
            this.refs.add(ref);
        }
        
        /**
         * @return the packageName
         */
        public final String getPackageName()
        {
            return packageName;
        }
        
        /**
         * @param packageName
         *            the packageName to set
         */
        public final void setPackageName(String packageName)
        {
            this.packageName = packageName;
        }
        
        /**
         * @return the className
         */
        public final String getClassName()
        {
            return className;
        }
        
        /**
         * @param className
         *            the className to set
         */
        public final void setClassName(String className)
        {
            this.className = className;
        }
        
        /**
         * @param code
         *            the code to set
         */
        public final void setCode(String code)
        {
            this.code = code;
        }
    }
    
    public static void main(String[] args)
    {
        String regex = "(.*\\s*)*com\\.lotomer;(.*\\s*)*";
        Pattern p = Pattern.compile(regex);
        
        String input = "asdfasdf\r\n\r\nimport com.lotomer;\r\n\r\nasdlfjasfd\r\n\r\naskdjfoaisdjf";
        Matcher m = p.matcher(input);
        ;
        System.out.println(m.matches());
        System.out.println("asdf\r\n\r\nasdf\r\n\r\nasdlfjasfd\r\n\r\naskdjfoais".matches("(.*\\s*.*)*"));
    }
}
