/**
 * 
 */
package org.tomstools.html.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tomstools.common.db.DataAccessor;
import org.tomstools.common.db.DataAccessorFactory;
import org.tomstools.common.db.exception.DAOException;
import org.tomstools.common.log.Logger;
import org.tomstools.common.util.Utils;
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
    
    /**
     * 获取机构交易汇总数据
     * @param beginDate     开始日期。格式：yyyy-MM-dd。不能为null或空字符串
     * @param endDate       结束日期。格式：yyyy-MM-dd。不能为null或空字符串
     * @param symbol        股票编号,可以为null或空字符串
     * @param agencyName    机构名称,可以为null或空字符串
     * @return 汇总结果
     */
    @SuppressWarnings("unchecked")
    public List<AgencyDeal> getAgencyDealList(String beginDate,String endDate,String symbol, String agencyName){
        if (Utils.isEmpty(beginDate) || Utils.isEmpty(endDate)){
            throw new RuntimeException("The beginDate and endDate cannot be empty!");
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("beginDate", beginDate);
        params.put("endDate", endDate);
        if (!Utils.isEmpty(symbol)){
            params.put("symbol", symbol);
        }
        if (!Utils.isEmpty(agencyName)){
            params.put("agencyName", agencyName);
        }
        DataAccessor dataAccessor = DataAccessorFactory.getInstance().getDataAccessorBuilder().newDataAccessor();
        List<AgencyDeal> result;
        try {
            dataAccessor.open(false);
            result = (List<AgencyDeal>) dataAccessor.query4list(SqlConstant.SELECT_AGENCY_DEAL, params);
            dataAccessor.close();
        } catch (DAOException e) {
            dataAccessor.rollback(false);
            LOG.error(e.getMessage(),e);
            result = Collections.emptyList();
        }
        
        return result;
    }
    
    /**
     * 获取知名机构交易汇总数据
     * @param beginDate     开始日期。格式：yyyy-MM-dd。不能为null或空字符串
     * @param endDate       结束日期。格式：yyyy-MM-dd。不能为null或空字符串
     * @param symbol        股票编号,可以为null或空字符串
     * @param agencyName    机构名称,可以为null或空字符串
     * @return 汇总结果
     */
    @SuppressWarnings("unchecked")
    public List<AgencyDeal> getAgencyDealListWithQ(String beginDate,String endDate,String symbol, String agencyName){
        if (Utils.isEmpty(beginDate) || Utils.isEmpty(endDate)){
            throw new RuntimeException("The beginDate and endDate cannot be empty!");
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("beginDate", beginDate);
        params.put("endDate", endDate);
        if (!Utils.isEmpty(symbol)){
            params.put("symbol", symbol);
        }
        if (!Utils.isEmpty(agencyName)){
            params.put("agencyName", agencyName);
        }
        DataAccessor dataAccessor = DataAccessorFactory.getInstance().getDataAccessorBuilder().newDataAccessor();
        List<AgencyDeal> result;
        try {
            dataAccessor.open(false);
            result = (List<AgencyDeal>) dataAccessor.query4list(SqlConstant.SELECT_AGENCY_DEAL_WITH_Q, params);
            dataAccessor.close();
        } catch (DAOException e) {
            dataAccessor.rollback(false);
            LOG.error(e.getMessage(),e);
            result = Collections.emptyList();
        }
        
        return result;
    }
    
    public static void main(String[] args) {
        AgencyDealDAO dao = new AgencyDealDAO();
        List<AgencyDeal> result = dao.getAgencyDealList("2013-09-01", "2013-09-11", "", "");
        for (AgencyDeal agencyDeal : result) {
            System.out.println(agencyDeal.toFrmtString());
        }
    }
}
