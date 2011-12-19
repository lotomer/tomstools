/**
 * copyright (a) 2010-2011 tomstools.org. All rights reserved.
 */
package org.tomstools.common.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.tomstools.common.log.Logger;

/**
 * 日期工具类
 * @author lotomer
 * @date 2011-12-14 
 * @time 下午02:36:43
 */
public class DateUtil {
    private static final Logger logger = Logger.getLogger(DateUtil.class);
    /** 默认时间格式 */
    public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /** 默认日期格式 */
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    
    private DateUtil(){}
    
    /**
     * 将时间转换为字符串
     * @param date  待转换时间
     * @param fmt   转换格式
     * @return 转换后的字符串
     */
    public static final String datetime2str(Date date,String fmt)
    {
        DateFormat sdf = new SimpleDateFormat(fmt);
        
        return sdf.format(date);
    }
    /**
     * 以默认个数将时间转换为字符串
     * @param date  待转换时间
     * @return 转换后的字符串
     */
    public static final String datetime2str(Date date)
    {
        return datetime2str(date, DEFAULT_DATETIME_FORMAT);
    }
    
    /**
     * 将字符串转换为时间
     * @param dateStr 时间字符串
     * @param fmt     字符串的时间格式
     * @return  时间
     */
    public static Date str2datetime(String dateStr, String fmt)
    {
        DateFormat sdf = new SimpleDateFormat(fmt);
        try
        {
            return sdf.parse(dateStr);
        }
        catch (ParseException e)
        {
            logger.error(e.getMessage(),e);
            return null;
        }
    }  
}
