/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.tomstools.common.Logger;
import org.tomstools.crawler.common.FieldSplitter;
import org.tomstools.crawler.common.String2DateTimeString;
import org.tomstools.crawler.common.ValueConvertible;
import org.tomstools.crawler.config.CrawlingRule;
import org.tomstools.crawler.config.Target;
import org.tomstools.crawler.extractor.ContentExtractor;
import org.tomstools.crawler.extractor.ContentExtractor.Field;
import org.tomstools.crawler.parser.HTMLParser;

/**
 * 0731fdc二手房咨询
 * 
 * @author admin
 * @date 2014年3月15日
 * @time 上午10:27:37
 * @version 1.0
 */
public class ESF0731fdc extends Target {
    private static final Logger LOGGER = Logger.getLogger(ESF0731fdc.class);
    public ESF0731fdc(CrawlingRule crawlingRule) {
        super();
        setName("0731fdc-esf");
        setUrl("http://esf.0731fdc.com/sale");
        setRegex4topDataFalg("\\[置顶\\]");
        setCrawlingRule(crawlingRule);
        setParser(new HTMLParser());
        //setContentPageExtractor(new NoSubpageExtractor()); 
      //XXX 需要修改，暂时屏蔽 setNavigationExtractor(new PageNavigationExtractor("div.pagination a:containsOwn(下一页)", "<a .*?href=\"(.*?)\">"));
        Map<String, ValueConvertible<String, String>> valueConverter = new HashMap<String, ValueConvertible<String,String>>();
        valueConverter.put("time", new String2DateTimeString("yyyy-MM-dd", "yyyy-MM-dd hh"));
        List<Field> fields = new ArrayList<Field>();
        fields.add(new ContentExtractor.TextField("title", "div.title>a>h5"));
        fields.add(new ContentExtractor.TextField("info", "div.title"));
        fields.add(new ContentExtractor.TextField("area", "div.area",new FieldSplitter("(\\S*).*", new int[]{0}, new String[]{"area"},null)));
        fields.add(new ContentExtractor.TextField("price", "div.price",new FieldSplitter("(\\S+)\\s+(\\S+)", new int[]{0,1}, new String[]{"totalPrice","price"},null)));
        fields.add(new ContentExtractor.TextField("time", "div.time",new FieldSplitter("(\\S+)\\s+(\\S+)\\s+(\\S+)", new int[]{0,1,2}, new String[]{"role","owner","time"},valueConverter)));
        
        setContentExtractor(new ContentExtractor("li>div.item",null,null,fields));
    }

    static class TaskData implements Callable<String>{
        private String data;
        public TaskData(String a){
            this.data =a ;
        }
        public String call() throws Exception {
            Thread.sleep(1000 * 5);
            return data;
        }
        
    }
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        TaskData taskData = new TaskData("aa");
        ExecutorService executor = Executors.newFixedThreadPool(1);
        Future<?> f = executor.submit(taskData);
        LOGGER.info("start");
//        while(!f.isDone()){
//            LOGGER.info("is not done!" + f);
//            Thread.sleep(500);
//        }
        LOGGER.info(f.get());
        LOGGER.info(executor.isShutdown());
        executor.shutdown();
        LOGGER.info(executor.isShutdown());
    }
}
