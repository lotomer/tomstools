/**
 * copyright (a) 2010-2011 tomstools.org. All rights reserved.
 */
package org.tomstools.ui.tags.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.tomstools.common.log.Logger;
import org.tomstools.common.merge.manage.WebFileManager;
import org.tomstools.common.merge.manage.WebFileManagerFactory;

/**
 * 样例。初始化标签数据信息
 * @author lotomer
 * @date 2011-12-22 
 * @time 上午11:02:59
 */
public class InitServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(InitServlet.class);
    private static final long serialVersionUID = -9015077898851101476L;

    @Override
    public void init() throws ServletException {        
        super.init();
        WebFileManager webFileManager = WebFileManagerFactory.getInstance().getWebFileManager();
        // 设置配置文件名
        webFileManager.addVariable("WEB_CONFIG_FILE", "config.properties");
        // 设置自定义变量
        webFileManager.addVariable("BASE_PATH", ".");
        //webFileManager.addVariable("LOCAL_PATH", ".");
        
        // 设置是否对文件进行压缩
        webFileManager.setNeedCompress(true);
        // 设置是否在压缩后删除原合并文件
        webFileManager.setNeedDeleteSourceFileForCompress(true);
        //执行初始化
        try {
            webFileManager.init();
        } catch (Exception e) {
            logger.fatal(e.getMessage(),e);
        }
    }
}
