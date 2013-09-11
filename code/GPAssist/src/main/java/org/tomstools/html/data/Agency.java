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
    private String agencySymbol;      // 标记
    private String sname;        // 名称
    private String path;        // 路径
    
    public Agency(String agencySymbol, String sname, String path) {
        super();
        this.agencySymbol = agencySymbol;
        this.sname = sname;
        this.path = path;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((agencySymbol == null) ? 0 : agencySymbol.hashCode());
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
        if (agencySymbol == null) {
            if (other.agencySymbol != null)
                return false;
        } else if (!agencySymbol.equals(other.agencySymbol))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Agency [symbol=" + agencySymbol + ", name=" + sname + ", path=" + path + "]";
    }

    public final String getAgencySymbol() {
        return agencySymbol;
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

    public final void setAgencySymbol(String agencySymbol) {
        this.agencySymbol = agencySymbol;
    }

    public final void setPath(String path) {
        this.path = path;
    }
    
}
