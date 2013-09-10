/**
 * 
 */
package org.tomstools.html.data;

import java.util.ArrayList;
import java.util.List;

/**
 * 机构交易数据
 * @author hadoop
 *
 */
public class AgencyDealDAO {
    private List<AgencyDeal> agencyDeals;
    
    public AgencyDealDAO() {
        super();
        agencyDeals = new ArrayList<AgencyDeal>();
    }

    public void save() {
        for (AgencyDeal agencyDeal : agencyDeals) {
            System.out.println(agencyDeal.getSymbol() + ":" + agencyDeal.getTdate() + ":" + agencyDeal.getBuy() + ":" + agencyDeal.getSale());
        }
    }

    public void add(AgencyDeal agencyDeal) {
        agencyDeals.add(agencyDeal);
    }
}
