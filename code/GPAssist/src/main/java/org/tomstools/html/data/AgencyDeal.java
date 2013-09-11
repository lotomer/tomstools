/**
 * 
 */
package org.tomstools.html.data;

/**
 * 机构交易数据
 * @author hadoop
 *
 */
public class AgencyDeal {
    private String symbol;      // 股票标记
    private String sname;       // 名称
    private String tdate;       // 日期
    private float buy;         // 买入
    private float sale;        // 卖出
    private String agencySymbol;// 机构标识
    public AgencyDeal(String agencySymbol,String symbol, String sname, String tdate, String buy, String sale) {
        super();
        this.agencySymbol = agencySymbol;
        this.symbol = symbol;
        this.sname = sname;
        this.tdate = tdate;
        this.buy = Float.parseFloat(buy);
        this.sale = Float.parseFloat(sale);
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
        result = prime * result + ((tdate == null) ? 0 : tdate.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AgencyDeal other = (AgencyDeal) obj;
        if (symbol == null) {
            if (other.symbol != null)
                return false;
        } else if (!symbol.equals(other.symbol))
            return false;
        if (tdate == null) {
            if (other.tdate != null)
                return false;
        } else if (!tdate.equals(other.tdate))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "AgencyDeal [symbol=" + symbol + ", sname=" + sname + ", tdate=" + tdate + ", buy="
                + buy + ", sale=" + sale + ", agencySymbol=" + agencySymbol + "]";
    }
    public final String getAgencySymbol() {
        return agencySymbol;
    }
    public final void setAgencySymbol(String agencySymbol) {
        this.agencySymbol = agencySymbol;
    }
    public final String getSymbol() {
        return symbol;
    }
    public final String getSname() {
        return sname;
    }
    public final String getTdate() {
        return tdate;
    }
    public final float getBuy() {
        return buy;
    }
    public final float getSale() {
        return sale;
    }
    public final void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    public final void setSname(String sname) {
        this.sname = sname;
    }
    public final void setTdate(String tdate) {
        this.tdate = tdate;
    }
    public final void setBuy(float buy) {
        this.buy = buy;
    }
    public final void setSale(float sale) {
        this.sale = sale;
    }
    
}
