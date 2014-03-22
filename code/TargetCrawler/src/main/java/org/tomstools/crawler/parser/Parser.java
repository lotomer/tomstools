/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.parser;

import org.tomstools.crawler.common.Element;




/**
 * 内容解析器接口
 * @author admin
 * @date 2014年3月12日 
 * @time 下午8:42:53
 * @version 1.0
 */
public interface Parser {
    /**
     * 根据参数解析内容
     * @param content 待解析的内容
     * @param param 参数
     * @return  解析后的内容。解析失败则返回null
     * @since 1.0
     */
    public Element parse(String content, String param);
    //public String parse(String content, String param);
    //public void parse(String content, ContentHandle handle, Map<String,String> params, boolean onlyText);
    /**
     * 根据参数解析内容
     * @param content   待解析的内容
     * @param params    参数列表
     * @param handle    解析出结果后的处理钩子
     * @param onlyText  是否仅保留文本（去除html标签等）
     * @since 1.0
     */
    //public void parse(String content, Map<String,String> params, ContentHandle handle);
    //public void parse(String content, ContentHandle handle);
}
