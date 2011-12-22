/**
 * copyright (a) 2010-2011 tomstools.org. All rights reserved.
 */
package org.tomstools.common.merge.manage;

import org.tomstools.common.merge.manager.datasource.DefaultWebFileManager;

/**
 * WEB前台文件管理工厂类
 * @author lotomer
 * @date 2011-12-21 
 * @time 上午11:30:27
 */
public final class WebFileManagerFactory {
    private static WebFileManagerFactory instance = new WebFileManagerFactory();
        
    public static WebFileManagerFactory getInstance(){
        return instance;
    }
    
    private WebFileManager webFileManager;    
    private WebFileManagerFactory(){
        // 使用默认。如需修改，使用setWebFileManager()进行替换
        webFileManager = new DefaultWebFileManager();
    }
    
    
    /** 设置web文件管理器。*/
    public void setWebFileManager(WebFileManager manager){
        this.webFileManager = manager;
    }

    public final WebFileManager getWebFileManager() {
        return webFileManager;
    }
}
