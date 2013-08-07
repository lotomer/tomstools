/**
 * copyright (a) 2010-2011 tomstools.org. All rights reserved.
 */
package org.tomstools.common.util;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 通用工具类
 * @author lotomer
 * @date 2011-12-14 
 * @time 下午02:42:29
 */
public final class Utils {
    private Utils(){}
    
    /**
     * 判断对象是否为空
     * @param data 待判断的对象
     * @return true 为null或仅包含空格的字符串； false 非空
     */
    public static boolean isEmpty(Object data){
        return (null == data || "".equals(data.toString().trim()));
    }
    
    /**
     * 判断结果集是否为空
     * @param <E> 泛型
     * @param data 结果集
     * @return true 结果集为null或空； false 非空
     */
    public static <E> boolean  isEmpty(Collection<E> data){
        return (null == data || 0 == data.size());
    }
    
    /**
     * 关闭流
     * 
     * @param c
     */
    public static void close(Closeable c) {
        if (null != c) {
            try {
                c.close();
                c = null;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 将字符串转换为列表
     * @param <T>
     * 
     * @param values
     *            待转换字符串
     * @param separator
     *            分隔符
     * @return 转换后的列表
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> strings2list(String values, String separator) {
        if (isEmpty(values)) {
          return new ArrayList<T>(0);
      }
        String[] array = values.split(separator);
        List<T> list = new ArrayList<T>(array.length);
        for (String system : array) {
            list.add((T) system.trim());
        }

        return list;
    }
    
    /**
     * 将整型转换为字节数组
     * 
     * @param intValue
     *            长整型数值
     * @return 字节数组
     */
    public static byte[] int2bytes(int intValue) {
        byte[] b = new byte[4];
        for (int i = 0; i < 4; ++i) {
            b[i] = (byte) ((intValue >>> (i * 8)) & 0xFF);
        }

        return b;
    }

    /**
     * 将字节数组转换为长整型
     * 
     * @param bytes
     *            字节数组
     * @return 长整型数组
     */
    public static int bytes2int(byte[] bytes) {
        int intValue = 0;
        for (int i = 0; i < 4; ++i) {
            intValue |= (bytes[i] & 0xFF) << (i * 8);
        }

        return intValue;
    }
    
    /**
     * 将html源代码编码，以便能够以源码查看的方式正常显示在其他html页面上
     * @param html  待编码的html源代码
     * @return  编码后的html源代码
     */
    public static final String encodeHTML(String html){
        if (!isEmpty(html)){
            return html.replaceAll("&", "&amp;").replaceAll("'", "&#39;").replaceAll("\"","&#34;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
        }else{
            return "";
        }
    }
    
    /**
     * 将编码后的html源代码转码
     * @param encodedHtml 经过编码的html源码
     * @return 转码后的html源码
     */
    public static final String decodeHTML(String encodedHtml){
        if (!isEmpty(encodedHtml)){
            return encodedHtml.replaceAll("&#39;", "'").replaceAll("\"","&#34;").replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&amp;", "&");
        }else{
            return "";
        }
    }
}
