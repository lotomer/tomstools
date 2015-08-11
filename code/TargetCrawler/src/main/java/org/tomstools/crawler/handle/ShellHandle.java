/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.handle;

import java.io.IOException;
import java.util.Arrays;
import java.util.StringTokenizer;

import org.tomstools.common.Logger;

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
    private boolean waitFor;
    /**
     * 构造函数
     * @param command   shell命令。执行时将加上完整的文件名参数
     * @since 1.0
     */
    public ShellHandle(String command){
        this.command = command;
        waitFor = false;
    }
    /*
     * @since 1.0
     */
    
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
            Runner runner = new Runner(cmdarray);
            Thread thread = new Thread(runner);
            thread.start();
            if (waitFor){
                while(thread.isAlive()){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        LOG.error(e.getMessage(),e);
                    }
                }
            }
        }
    }

    static class Runner implements Runnable{
        private static final Logger LOG = Logger.getLogger(ShellHandle.class);
        private String[] cmdarray;

        public Runner(String[] cmdarray) {
            this.cmdarray = cmdarray;
        }

        
        public void run() {
            LOG.info("execute command: " + Arrays.toString(cmdarray));
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
    
    
    /**
     * @param waitFor 设置 waitFor
     * @since 1.0
     */
    public final void setWaitFor(boolean waitFor) {
        this.waitFor = waitFor;
    }
    public static void main(String[] args) throws InterruptedException {
        ShellHandle s = new ShellHandle("cmd /c e:/work/todatabase.bat 11");
        s.setWaitFor(false);
        s.handle("10");
        System.out.println("9999999999999999999999999");
        Thread.sleep(3000);
        ShellHandle ss = new ShellHandle("cmd /c e:/work/todatabase.bat 22");
        ss.setWaitFor(false);
        ss.handle("15");
        System.out.println("777");
        //System.exit(1);
    }
}
