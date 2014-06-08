/**
 * copyright (a) 2010-2011 tomstools.org. All rights reserved.
 */
package org.tomstools.prediction.common;

/**
 * 日志管理类
 * @author lotomer
 * @date 2011-12-14 
 * @time 上午10:31:16
 */
public class Logger {
    public static <T> Logger getLogger(Class<T> clazz)
    {
        return new Logger(clazz);
    }
    public static Logger getLogger(String clazzName)
    {
        return new Logger(clazzName);
    }
        
    private org.apache.log4j.Logger logger; 

    protected <T> Logger(Class<T> clazz)
    {
        logger = org.apache.log4j.Logger.getLogger(clazz.getName());
    }
    
    protected Logger(String clazzName)
    {
        logger = org.apache.log4j.Logger.getLogger(clazzName);
    }
    
    public void debug(Object message)
    {
        logger.debug(message);
    }
    
    public void debug(Object message, Throwable t)
    {
        logger.debug(message, t);
    }
    
    public void info(Object message)
    {
        logger.info(message);
    }

    public void info(Object message, Throwable t)
    {
        logger.info(message, t);
    }
    
    public void warn(Object message)
    {
        logger.warn(message);
    }

    public void warn(Object message, Throwable t)
    {
        logger.warn(message, t);
    }
    
    public void error(String message)
    {
        logger.error(message);
    }

    public void error(String message, Throwable t)
    {
        logger.error(message, t);
    }
    
    public void fatal(String message)
    {
        logger.fatal(message);
    }

    public void fatal(String message, Throwable t)
    {
        logger.fatal(message, t);
    }
    
    public void test(String message)
    {
        logger.warn("[========TEST==========] " + message);
    }
}
