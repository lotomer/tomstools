/**
 * 
 */
package org.tomstools.html.data;

/**
 * 机构交易数据
 * 
 * @author hadoop
 * 
 */
public class AgencyDeal {
    private String symbol; // 股票标记
    private String sname; // 名称
    private String tdate; // 日期
    private long buy; // 买入
    private long sale; // 卖出
    private String agencySymbol;// 机构标识
    private float total;
    private String agencyName;

    public AgencyDeal() {

    }

    public AgencyDeal(String agencySymbol, String symbol, String sname, String tdate, String buy,
            String sale) {
        super();
        this.agencySymbol = agencySymbol;
        this.symbol = symbol;
        this.sname = sname;
        this.tdate = tdate;
        this.buy = (long) (Float.parseFloat(buy) * 10000);
        this.sale = (long) (Float.parseFloat(sale) * 10000);
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

    public String toFrmtString() {
        StringBuilder msg = new StringBuilder();
        msg.append(symbol);
        msg.append("\t");
        msg.append(buy);
        msg.append("\t");
        msg.append(sale);
        msg.append("\t");
        msg.append(total);
        msg.append("\t");
        msg.append(sname);
        msg.append("\t");
        msg.append(agencyName);

        return msg.toString();
    }

     @Override
     public String toString() {
     return "AgencyDeal [symbol=" + symbol + ", sname=" + sname + ", tdate=" +
     tdate + ", buy="
     + buy + ", sale=" + sale + ", agencySymbol=" + agencySymbol + ", total="
     + total
     + ", agencyName=" + agencyName + "]";
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

    public final long getBuy() {
        return buy;
    }

    public final long getSale() {
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

    public final void setBuy(long buy) {
        this.buy = buy;
    }

    public final void setSale(long sale) {
        this.sale = sale;
    }

    public final float getTotal() {
        return total;
    }

    public final void setTotal(float total) {
        this.total = total;
    }

    public final String getAgencyName() {
        return agencyName;
    }

    public final void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

}
