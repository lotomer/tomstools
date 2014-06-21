/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.handle;

import java.io.IOException;
import java.util.StringTokenizer;

import org.tomstools.crawler.common.Logger;

/**
 * shell命令钩子。使用shell命令来执行
 * @author admin
 * @date 2014年6月21日 
 * @time 下午10:17:33
 * @version 1.0
 */
public class ShellHandle implements CompletedHandleable {
    private static final Logger LOG = Logger.getLogger(ShellHandle.class);
    private String command;
    /**
     * 构造函数
     * @param command   shell命令。执行时将加上完整的文件名参数
     * @since 1.0
     */
    public ShellHandle(String command){
        this.command = command;
    }
    /*
     * @since 1.0
     */
    @Override
    public void handle(String flag) {
        if (null != command){
            if (command.length() == 0)
                throw new IllegalArgumentException("Empty command");

            StringTokenizer st = new StringTokenizer(command);
            String[] cmdarray = new String[st.countTokens() + 1];
            for (int i = 0; st.hasMoreTokens(); i++){
                cmdarray[i] = st.nextToken();
            }
            
            cmdarray[cmdarray.length - 1] = flag;
            try {
                Process process = Runtime.getRuntime().exec(cmdarray);
                if (null != process){
                    int exitValue = process.waitFor();
                    LOG.info("process exit value: " + exitValue);
                }
            } catch (IOException e) {
                LOG.warn(e.getMessage(),e);
            } catch (InterruptedException e) {
                LOG.warn(e.getMessage(),e);
            }
        }
    }

    public static void main(String[] args) {
        ShellHandle s = new ShellHandle("cmd /c e:/work/todatabase.bat");
        s.handle("asdf we.csv");
        System.out.println("9999999999999999999999999");
        System.exit(1);
    }
}
