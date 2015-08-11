/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.hive.udf.busi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 模糊匹配
 * @author admin
 * @date 2014年11月14日
 * @time 下午1:09:10
 * @version 1.0
 */
public class FuzzyMatcher {
    static FuzzyMatcher instance = null;
    Map<Integer, String> appRules;

    public static FuzzyMatcher getInstance(String driver, String url, String user, String password)
            throws ClassNotFoundException, SQLException {
        if (null == instance) {
            synchronized (FuzzyMatcher.class) {
                if (null == instance) {
                    instance = new FuzzyMatcher(driver, url, user, password);
                }
            }
        }
        return instance;
    }

    FuzzyMatcher() {
    }

    private FuzzyMatcher(String driver, String url, String user, String password)
            throws ClassNotFoundException, SQLException {
        Class.forName(driver);
        Connection conn = DriverManager.getConnection(url, user, password);

        this.appRules = new HashMap<>();

        String sql = "select ID,UA from UA_MOBILE ";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet result = pstmt.executeQuery();
        while (result.next()) {
            String ua = result.getString("UA");
            if (null != ua && ua.trim().length() != 0) {
                appRules.put(result.getInt("ID"), ua.trim().toLowerCase());
            }
        }
        result.close();
        conn.close();
    }

    public int match(String input) {
        // 输入不能为空
        if (null == input || input.trim().length() == 0) {
            return -1;
        }

        String src = input.toLowerCase();

        // 匹配规则
        int retValue = -1;
        int maxSize = 0;
        String value;
        for (Entry<Integer, String> rule : this.appRules.entrySet()) {
            value = rule.getValue();
            // 长度超过已经匹配上的长度，并且也匹配上了
            if (maxSize < value.length() && src.contains(value)) {
                retValue = rule.getKey();
                maxSize = value.length();
            }
        }

        return retValue;
    }
}
