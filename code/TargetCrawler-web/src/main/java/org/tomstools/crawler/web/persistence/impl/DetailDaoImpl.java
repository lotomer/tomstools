/**
 * copyright (a) 2010-2015 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.web.persistence.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.tomstools.crawler.web.persistence.DetailDao;

@Repository("detailDao") 
public class DetailDaoImpl<Detail> implements DetailDao<Detail> {
    @Autowired  
    private SqlSessionFactory sqlSessionFactory;  
    public List<Detail> select(Integer crawlId, String startTime, String endTime,
            RowBounds rowBounds) {
        Map<String,Object> params = new HashMap<String,Object>();
        if (null != crawlId){
            params.put("crawl_id", crawlId);
        }
        
        if (null != startTime){
            params.put("start_time", startTime);
        }if (null != endTime){
            params.put("end_time", endTime);
        }
        
        List<Detail> result = sqlSessionFactory.openSession().selectList("selectDetail",params,rowBounds);
        if (null != result){
            return result;
        }else{
            return Collections.emptyList();
        }
    }
    public int count(Integer crawlId, String startTime, String endTime) {
        Map<String,Object> params = new HashMap<String,Object>();
        if (null != crawlId){
            params.put("crawl_id", crawlId);
        }
        
        if (null != startTime){
            params.put("start_time", startTime);
        }if (null != endTime){
            params.put("end_time", endTime);
        }
        
        Integer result = sqlSessionFactory.openSession().selectOne("selectDetailCount",params);
        if (null != result){
            return result;
        }else{
            return 0;
        }
    }

}
