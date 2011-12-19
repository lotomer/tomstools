/**
 * copyright (a) 2010-2011 tomstools.org. All rights reserved.
 */
package org.tomstools.ui.tags.handle;

/**
 * 列表钩子管理器
 * @author lotomer
 * @date 2011-12-15 
 * @time 下午11:30:24
 */
public class ListHandleManager {
    private static ListTagHandle handle = null;

    public static final ListTagHandle getHandle() {
        return handle;
    }

    /**
     * 设置钩子。必须在程序启动时设置该钩子，否则会空指针异常
     * @param handle
     */
    public static final void setHandle(ListTagHandle handle) {
        ListHandleManager.handle = handle;
    }
    
}
