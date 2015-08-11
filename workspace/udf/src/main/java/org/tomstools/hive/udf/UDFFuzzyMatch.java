/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.hive.udf;

import java.sql.SQLException;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.tomstools.hive.udf.busi.FuzzyMatcher;

/**
 * UDF：模糊匹配
 * 
 * @author admin
 * @date 2014年8月4日
 * @time 下午9:36:28
 * @version 1.0
 */
@Description(name = "fuzzy_match", value = "_FUNC_(jdbcDriver,jdbcUrl,jdbcUser,jdbcPassword,fieldValue) - Returns -1 if not matched!", extended = "")
public class UDFFuzzyMatch extends UDF {
    public int evaluate(String jdbcDriver, String jdbcUrl, String user, String password,
            String fieldValue) throws ClassNotFoundException, SQLException {

        return FuzzyMatcher.getInstance(jdbcDriver, jdbcUrl, user, password).match(fieldValue);
    }
}
