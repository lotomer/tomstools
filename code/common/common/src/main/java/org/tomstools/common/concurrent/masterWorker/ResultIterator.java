/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.common.concurrent.masterWorker;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * master-worker模式的结果收集器
 * @author admin
 * @date 2014年4月2日 
 * @time 下午2:17:15
 * @version 1.0
 */
public class ResultIterator<OUT> implements Iterator<OUT> {
    private Master<?, OUT> master;
    private Map<String, OUT> resultData;
    private OUT value;

    public ResultIterator(Master<?, OUT> master) {
        this.master = master;
        //resultData = master.getResult();
    }

    public boolean hasNext() {
        value = null;
        while (!resultData.isEmpty() || !master.isComplete()) {
            // 收集结果，并在收集之后删除
            Set<String> keys = resultData.keySet();
            String key = null;
            for (String k : keys) {
                // 一次只处理一条
                key = k;
                break;
            }
            if (null != key) {
                value = resultData.remove(key);
                break;
            }
        }
        
        return null != value;
    }

    public void remove() {
        // do nonthing
    }

    public OUT next() {
        return value;
    }
}