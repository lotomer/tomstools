/**
 * copyright (a) 2010-2012 tomstools.org. All rights reserved.
 */
package org.tomstools.common.db.exception;

/**  
 * 数据访问异常类
 * @author lotomer
 * @date 2012-3-22 
 * @time 上午11:04:28
 */
public class DAOException extends Exception 
{
    private static final long serialVersionUID = 5256234211420495466L;

    public DAOException()
    {
        super();
    }

    public DAOException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public DAOException(String message)
    {
        super(message);
    }

    public DAOException(Throwable cause)
    {
        super(cause);
    }
    
    
}
