package org.tomstools.common.db.result;

import java.io.Serializable;

/**  
 * ClassName:   PageInfo
 * Description: 
 * Copyright:   (c)2010 lotomer.org 
 * 
 * @author:     lotomer
 * @version:    1.0  
 * Create at:   2010-7-29 下午02:44:24  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * --------------------------------------------------------  
 * 2010-7-29      lotomer     1.0          1.0 Version  
 */
public class PageInfo implements Serializable
{
    private static final long serialVersionUID = -8309428162684983844L;
    /** 默认页面最大值 */
    public static PageInfo DEFAULT = new PageInfo();
    
    private final int DEFAULT_PAGE_SIZE = 1024;
    
    /** 页面大小 */
    private int pageSize = DEFAULT_PAGE_SIZE;
    
    /** 页面序号 */
    private int pageNum = 1;

    /**
     * @return 页面大小
     */
    public final int getPageSize()
    {
        return pageSize;
    }

    /**
     * @param pageSize 设置页面大小
     */
    public final void setPageSize(int pageSize)
    {
        this.pageSize = pageSize;
    }

    /**
     * @return 页面序号。起始序号为1，表示第一页。
     */
    public final int getPageNum()
    {
        return pageNum;
    }

    /**
     * @param pageNum 设置页面序号。起始序号为1，表示第一页。
     */
    public final void setPageNum(int pageNum)
    {
        this.pageNum = pageNum;
    }
    
    @Override
    public String toString()
    {
        return PageInfo.class.getName() + ":[pageNum:" + pageNum + ",pageSize:" + pageSize + "]";
    }
}
