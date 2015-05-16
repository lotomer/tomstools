/**
 * copyright (a) 2010-2015 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.web.persistence.impl;

import java.util.Collections;
import java.util.List;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.tomstools.crawler.web.persistence.StatusDao;

@Repository("statusDao") 
public class StatusDaoImpl<Status> implements StatusDao<Status> {
    @Autowired  
    private SqlSessionFactory sqlSessionFactory;  
    public List<Status> select(String status) {
        List<Status> result = null;
        if (null == status){
            result = sqlSessionFactory.openSession().selectList("selectStatus");
        }else{
            result = sqlSessionFactory.openSession().selectList("selectStatusByOldStatus",status);
        }
        if (null != result){
            return result;
        }else{
            return Collections.emptyList();
        }
    }

}
