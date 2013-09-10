/**
 * 
 */
package org.tomstools.html.data;

/**
 * 机构信息
 * @author hadoop
 *
 */
public class Agency {
    private String symbol;      // 标记
    private String sname;        // 名称
    private String path;        // 路径
    
    public Agency(String symbol, String sname, String path) {
        super();
        this.symbol = symbol;
        this.sname = sname;
        this.path = path;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
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
        Agency other = (Agency) obj;
        if (symbol == null) {
            if (other.symbol != null)
                return false;
        } else if (!symbol.equals(other.symbol))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Agency [symbol=" + symbol + ", name=" + sname + ", path=" + path + "]";
    }

    public final String getSymbol() {
        return symbol;
    }
    public final String getPath() {
        return path;
    }

    public final String getSname() {
        return sname;
    }

    public final void setSname(String sname) {
        this.sname = sname;
    }

    public final void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public final void setPath(String path) {
        this.path = path;
    }
    
}
