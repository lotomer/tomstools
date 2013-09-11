/**
 * 
 */
package org.tomstools.html.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tomstools.common.db.DataAccessor;
import org.tomstools.common.db.DataAccessorFactory;
import org.tomstools.common.db.exception.DAOException;
import org.tomstools.common.log.Logger;
import org.tomstools.html.constant.SqlConstant;

/**
 * 机构交易数据
 * @author hadoop
 *
 */
public class AgencyDealDAO {
    private static final Logger LOG = Logger.getLogger(AgencyDealDAO.class);
    private List<AgencyDeal> agencyDeals;
    
    public AgencyDealDAO() {
        super();
        agencyDeals = new ArrayList<AgencyDeal>();
    }

    public void save() {
        DataAccessor dataAccessor = DataAccessorFactory.getInstance().getDataAccessorBuilder().newDataAccessor();
        try {
            dataAccessor.open(false);
            for (AgencyDeal agencyDeal : agencyDeals) {
                //Object obj = dataAccessor.query(SqlConstant.SELECT_ONE_AGENCY, agency);
                //if (null == obj){
                    // 不存在，则新增
                    dataAccessor.insert(SqlConstant.INSERT_AGENCY_DEAL, agencyDeal);
                //}
            }
            dataAccessor.commit();
            dataAccessor.close();
        } catch (DAOException e) {
            dataAccessor.rollback(false);
            LOG.error(e.getMessage(),e);
        }
        
    }

    public void add(AgencyDeal agencyDeal) {
        agencyDeals.add(agencyDeal);
    }

    /**
     * 清除指定日期的交易数据
     * @param beginDate 开始日期
     * @param endDate   结束日期
     */
    public void clean(String beginDate, String endDate) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("beginDate", beginDate);
        params.put("endDate", endDate);
        DataAccessor dataAccessor = DataAccessorFactory.getInstance().getDataAccessorBuilder().newDataAccessor();
        try {
            dataAccessor.open(false);
            
            dataAccessor.delete(SqlConstant.CLEAN_AGENCY_DEALS, params);
            
            dataAccessor.commit();
            dataAccessor.close();
        } catch (DAOException e) {
            dataAccessor.rollback(false);
            LOG.error(e.getMessage(),e);
        }
    }
    
    
}
