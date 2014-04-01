/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.parser;

import org.tomstools.crawler.common.Element;
import org.tomstools.crawler.common.ElementProcessor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * json解析器
 * @author admin
 * @date 2014年3月12日 
 * @time 下午8:47:00
 * @version 1.0
 */
public class JSONParser implements Parser {
    @Override
    public Element parse(String content, String param) {
        JSONObject obj = JSON.parseObject(content);
        if (null != obj){
            return new JsonElement(obj.getJSONObject(param));
        }else{
            return null;
        }
    }

//    public void parse(String content, Map<String, String> params, ContentHandle handle) {
//        JSONObject obj = JSON.parseObject(content);
//        if (null != obj){
//            for (Entry<String, String> entry : params.entrySet()) {
//                handle.handle(entry.getKey(), new JsonElement(obj.getJSONObject(entry.getValue())));
//            }
//        }
//    }

    public static class JsonElement implements Element{
        private JSONObject obj;
        
        /**
         * @param obj
         * @since 1.0
         */
        public JsonElement(JSONObject obj) {
            super();
            this.obj = obj;
        }

        @Override
        public String getCode() {
            return obj.toJSONString();
        }

        @Override
        public String getAttribute(String attributeName) {
            return obj.getString(attributeName);
        }

        @Override
        public String getText() {
            return obj.toString();
        }

        @Override
        public void select(String cssQuery, ElementProcessor processor) {
            // TODO Auto-generated method stub
            
        }
        
    }
}
