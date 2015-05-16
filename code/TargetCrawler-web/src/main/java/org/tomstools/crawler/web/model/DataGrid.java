/**
 * copyright (a) 2010-2015 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.web.model;

import java.util.List;

/**
 * 
 * @author admin
 * @date 2015年5月16日 
 * @time 下午7:45:57
 * @version 1.0
 */
public class DataGrid {
    private int total;
    private List<?> rows;
    public final int getTotal() {
        return total;
    }
    public final DataGrid setTotal(int total) {
        this.total = total;
        return this;
    }
    public final List<?> getRows() {
        return rows;
    }
    public final DataGrid setRows(List<?> rows) {
        this.rows = rows;
        return this;
    }
}
