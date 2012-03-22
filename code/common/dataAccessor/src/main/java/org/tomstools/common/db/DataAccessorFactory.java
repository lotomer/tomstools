/**
 * copyright (a) 2010-2012 tomstools.org. All rights reserved.
 */
package org.tomstools.common.db;

import java.util.HashMap;
import java.util.Map;

import org.tomstools.common.db.impl.MyBatisDataAccessorBuilder;

/**
 * 数据库访问工厂单例
 * @author lotomer
 * @date 2012-3-22 
 * @time 上午11:04:43
 */
public final class DataAccessorFactory {
    private static DataAccessorFactory instance = new DataAccessorFactory();
    private Map<String, DataAccessorBuilder> dataAccessorBuilders = new HashMap<String, DataAccessorBuilder>();

    /**
     * 获取单例
     */
    public static DataAccessorFactory getInstance() {
        return instance;
    }

    private DataAccessorFactory() {
        dataAccessorBuilders.put(MyBatisDataAccessorBuilder.KEY_DB_CONFIG_FILE_NAME,
                new MyBatisDataAccessorBuilder());
    }

    /**
     * 获取默认数据库访问对象创建器
     * 
     * @return 数据库访问工厂
     */
    public final DataAccessorBuilder getDataAccessorBuilder() {
        return dataAccessorBuilders.get(MyBatisDataAccessorBuilder.KEY_DB_CONFIG_FILE_NAME);
    }

    /**
     * 根据环境编号获取数据库访问对象创建器
     * 
     * @return 数据库访问工厂
     */
    public final DataAccessorBuilder getDataAccessorBuilder(String environment) {
        DataAccessorBuilder builder = dataAccessorBuilders.get(environment);
        if (null == builder) {
            builder = new MyBatisDataAccessorBuilder(environment);
            dataAccessorBuilders.put(environment, builder);
        }
        return builder;
    }

    /**
     * 指定数据库连接信息生成数据库访问对象创建器
     * @param driver    驱动程序
     * @param url       数据库连接url
     * @param user      数据库用户名
     * @param password  数据库用户密码
     * @return 数据库访问生成器
     */
    public final DataAccessorBuilder getDataAccessorBuilder(String driver, String url,
            String user, String password) {
        StringBuilder id = new StringBuilder();
        id.append(url);
        id.append("_");
        id.append(user);
        DataAccessorBuilder builder = dataAccessorBuilders.get(id.toString());
        if (null == builder) {
            builder = new MyBatisDataAccessorBuilder(id.toString(), driver, url, user, password);
            dataAccessorBuilders.put(id.toString(), builder);
        }
        return builder;
    }
}
