/**
 * 
 */
package org.tomstools.web.model;

/**
 * WEB指标内容
 * @author lotomer
 *
 */
public class WebMetric {
	private String title;
	private Object value;
	private String selector;
	private int orderNum;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public String getSelector() {
		return selector;
	}
	public void setSelector(String selector) {
		this.selector = selector;
	}
	public int getOrderNum() {
		return orderNum;
	}
	public void setOrderNum(int orderNum) {
		this.orderNum = orderNum;
	}
    public WebMetric copyValue() {
        WebMetric aWebMetric = new WebMetric();
        aWebMetric.setOrderNum(orderNum);
        aWebMetric.setTitle(title);
        return aWebMetric;
    }
	
}
