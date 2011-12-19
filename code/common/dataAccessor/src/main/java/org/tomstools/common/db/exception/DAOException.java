package org.tomstools.common.db.exception;

/**  
 * ClassName:   DAOException
 * Description: 数据访问异常类
 * Copyright:   (c)2010 lotomer.org 
 * 
 * @author:     lotomer
 * @version:    1.0  
 * Create at:   2010-7-29 下午02:20:09  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * --------------------------------------------------------  
 * 2010-7-29      lotomer     1.0          1.0 Version  
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
