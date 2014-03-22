/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.spring;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * @author admin
 * @date 2014年3月16日 
 * @time 下午10:51:50
 * @version 1.0
 */
public class ApplicationContext {

    private ClassPathXmlApplicationContext context;
    
    private static volatile ApplicationContext instance ;
    
    private static final String DEFALUT_CONFIG_FILE = "classpath:applicationContext.xml";
    
    public ApplicationContext() {
        super();
        String configFile = System.getProperty("SPRING_CONFIG_FILE");
        if (null == configFile){
            context = new ClassPathXmlApplicationContext(DEFALUT_CONFIG_FILE);
        }else{
            context = new ClassPathXmlApplicationContext("classpath:"+configFile);
        }
        
        context.start();
        //context.getBeanFactory().setCacheBeanMetadata(false);
    }

//    public void configureBean(String beanName,Object obj){
//        context.getBeanFactory().configureBean(obj, beanName);
//    }
//    public void refresh(){
//        context.getBeanFactory().registerSingleton(ConfigurableApplicationContext.SYSTEM_PROPERTIES_BEAN_NAME, System.getProperties());
//    }
    public static ApplicationContext getInstance() {
        if (instance == null) {
            synchronized(ApplicationContext.class) {
                if (instance == null) {
                    instance = new ApplicationContext();
                }
            }
        }
        return instance;
    }
    
    public Object getBean(String beanName) {
        return context.getBean(beanName);
    }
}
