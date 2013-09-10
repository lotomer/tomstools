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
    private String symbol;      // 标记
    private String sname;       // 名称
    private String tdate;       // 日期
    private String buy;         // 买入
    private String sale;        // 卖出
    public AgencyDeal(String symbol, String sname, String tdate, String buy, String sale) {
        super();
        this.symbol = symbol;
        this.sname = sname;
        this.tdate = tdate;
        this.buy = buy;
        this.sale = sale;
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
                + buy + ", sale=" + sale + "]";
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
    public final String getBuy() {
        return buy;
    }
    public final String getSale() {
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
    public final void setBuy(String buy) {
        this.buy = buy;
    }
    public final void setSale(String sale) {
        this.sale = sale;
    }
    
}
