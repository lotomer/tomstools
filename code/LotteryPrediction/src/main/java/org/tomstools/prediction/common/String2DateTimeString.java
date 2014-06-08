/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.prediction.common;

/**
 * 字符串转换为时间字符串
 * 
 * @author admin
 * @date 2014年3月16日
 * @time 下午9:31:11
 * @version 1.0
 */
public class String2DateTimeString implements ValueConvertible<String, String> {
    private String2DateTime s2d;
    private DateTime2String d2s;

    /**
     * 
     * @param inputFormat 时间格式。不能为null。 <br>
     *            如果转换失败，则使用相对时间，暂时支持 (月|周|天|小时|分钟|秒)(前|后)，比如 10秒前、1小时后
     * @param outputFormat 日期转换为字符串的输出格式
     * @since 1.0
     */
    public String2DateTimeString(String inputFormat, String outputFormat) {
        super();
        s2d = new String2DateTime(inputFormat);
        d2s = new DateTime2String(outputFormat);
    }

    public String valueOf(String v) {
        return d2s.valueOf(s2d.valueOf(v));
    }

}
