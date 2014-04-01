/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.common;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间转换为字符串
 * @author admin
 * @date 2014年3月16日 
 * @time 下午9:27:40
 * @version 1.0
 */
public class DateTime2String implements ValueConvertible<Date, String> {
    private SimpleDateFormat dateFormat;
    /**
     * 
     * @param format 日期转换为字符串的输出格式
     * @since 1.0
     */
    public DateTime2String(String format) {
        super();
        dateFormat = new SimpleDateFormat(format);
    }

    @Override
    public String valueOf(Date v) {
        return dateFormat.format(v);
    }

}
