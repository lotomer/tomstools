/**
 * 
 */
package org.tomstools.html.data;

/**
 * 股票数据
 * 
 * @author hadoop
 * 
 */
public class StockDeal {
    private String symbol; // 股票标记
    private String sname; // 名称
    private String tdate; // 日期
    private int price; // 收盘价格
    private int amplitude; // 涨跌幅

    public StockDeal() {

    }

    public StockDeal(String symbol, String sname, String tdate, String price, String amplitude) {
        super();
        this.symbol = symbol;
        this.sname = sname;
        this.tdate = tdate;
        this.price = (int) (Float.parseFloat(price) * 100);
        this.amplitude = (int) (Float.parseFloat(amplitude) * 10000);
    }

    public final String getSymbol() {
        return symbol;
    }

    public final void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public final String getSname() {
        return sname;
    }

    public final void setSname(String sname) {
        this.sname = sname;
    }

    public final String getTdate() {
        return tdate;
    }

    public final void setTdate(String tdate) {
        this.tdate = tdate;
    }

    public final int getPrice() {
        return price;
    }

    public final void setPrice(int price) {
        this.price = price;
    }

    public final int getAmplitude() {
        return amplitude;
    }

    public final void setAmplitude(int amplitude) {
        this.amplitude = amplitude;
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
        StockDeal other = (StockDeal) obj;
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
        return "StockDeal [symbol=" + symbol + ", sname=" + sname + ", tdate=" + tdate + ", price="
                + price + ", amplitude=" + amplitude + "]";
    }
}
