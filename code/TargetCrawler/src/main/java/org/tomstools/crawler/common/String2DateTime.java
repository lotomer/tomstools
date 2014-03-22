/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串转换为时间
 * 
 * @author admin
 * @date 2014年3月16日
 * @time 下午8:25:30
 * @version 1.0
 */
public class String2DateTime implements ValueConvertible<String, Date> {
    private static final Logger LOGGER = Logger.getLogger(String2DateTime.class);
    private SimpleDateFormat dateFormat; // 日期格式化对象
    private static final Pattern RELATIVE_TIME_PATTERN = Pattern
            .compile("(\\d+)(月|周|天|小时|分钟|秒)[以]{0,1}(前|后)"); // 相对时间正则表达式解析器

    /**
     * @param format 时间格式。不能为null。 <br>
     *            如果转换失败，则使用相对时间，暂时支持 (月|周|天|小时|分钟|秒)(前|后)，比如 10秒前、1小时后
     * @since 1.0
     */
    public String2DateTime(String format) {
        super();
        dateFormat = new SimpleDateFormat(format);
    }

    public Date valueOf(String v) {
        try {
            return dateFormat.parse(v);
        } catch (ParseException e) {
            // 使用相对时间
            Calendar c = Calendar.getInstance();
            // 解析字符串
            Matcher m = RELATIVE_TIME_PATTERN.matcher(v);
            if (m.find()) {
                // 匹配上，则获取相应的值
                int num;
                if ("前".equals(m.group(3))) {
                    num = Integer.parseInt("-" + m.group(1));
                } else {
                    num = Integer.parseInt(m.group(1));
                }
                // 根据数据量优先顺序为
                if ("月".equals(m.group(2))) {
                    c.add(Calendar.MONTH, num);
                } else if ("周".equals(m.group(2))) {
                    c.add(Calendar.DATE, num * 7);
                } else if ("天".equals(m.group(2))) {
                    c.add(Calendar.DATE, num);
                } else if ("小时".equals(m.group(2))) {
                    c.add(Calendar.HOUR, num);
                } else if ("分钟".equals(m.group(2))) {
                    c.add(Calendar.MINUTE, num);
                } else if ("秒".equals(m.group(2))) {
                    c.add(Calendar.SECOND, num);
                }
            } else {
                LOGGER.error("invalid datetime string: " + v);
            }

            return c.getTime();
        }
    }

    public static void main(String[] args) {
        String v = "10秒以前";// "20140104";
        String format = null;// "yyyyMMdd";
        System.out.println("now:" + new Date());
        System.out.println("    " + new String2DateTime(format).valueOf(v));
    }
}
