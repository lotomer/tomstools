package org.tomstools.html.data;

import java.util.HashSet;
import java.util.Set;

import org.tomstools.common.db.DataAccessor;
import org.tomstools.common.db.DataAccessorFactory;
import org.tomstools.common.db.exception.DAOException;
import org.tomstools.common.log.Logger;
import org.tomstools.html.constant.SqlConstant;

/**
 * 机构数据
 * @author hadoop
 *
 */
public class AgencyDAO {
    private static final Logger LOG = Logger.getLogger(Agency.class);
    private Set<Agency> agencies;
    
    public AgencyDAO(){
        agencies = new HashSet<Agency>();
    }
    /**
     * 添加一条机构信息
     * @param name  机构名称
     * @param path  路径
     */
    public void add(Agency agency) {
        if (null == agency){
            return;
        }
        agencies.add(agency);
    }

    /**
     * 保存数据
     */
    public void save(){
        int newCount = 0;
        Agency tmpAgency = null;
        DataAccessor dataAccessor = DataAccessorFactory.getInstance().getDataAccessorBuilder().newDataAccessor();
        try {
            dataAccessor.open();
            for (Agency agency : agencies) {
            	tmpAgency = agency;
                Object obj = dataAccessor.query(SqlConstant.SELECT_ONE_AGENCY, agency);
                if (null == obj){
                    // 不存在，则新增
                    dataAccessor.insert(SqlConstant.INSERT_AGENCY, agency);
                    ++newCount;
                }
                //System.out.println(agency.getSymbol() + ":" + agency.getSname() + ":"+ this.host + agency.getPath());
            }
            dataAccessor.commit();
            dataAccessor.close();
        } catch (DAOException e) {
            dataAccessor.rollback(false);
            LOG.error(tmpAgency.toString());
            LOG.error(e.getMessage(),e);
        }
        
        LOG.info("total:" + agencies.size() + ", new: " + newCount);
    }
}
