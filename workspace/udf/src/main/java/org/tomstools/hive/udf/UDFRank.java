/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.hive.udf;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.tomstools.hive.udf.busi.Rank;

/**
 * UDF：排名
 * @author admin
 * @date 2014年8月4日
 * @time 下午9:36:28
 * @version 1.0
 */
@Description(name = "rank", value = "_FUNC_(column1,...) - Returns rank of the same column's", extended = "")
public class UDFRank extends UDF {
    public int evaluate(Object... args) {
        return Rank.execute(args);
    }
}
