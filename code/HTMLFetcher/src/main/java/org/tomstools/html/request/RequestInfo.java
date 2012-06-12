/**
 * copyright (a) 2010-2012 tomstools.org. All rights reserved.
 */
package org.tomstools.html.request;

import org.tomstools.html.form.Form;
import org.tomstools.html.header.Headers;

/**
 * @author lotomer
 * @date 2012-6-12 
 * @time 下午02:57:26
 */
public class RequestInfo {
    private Form form;
    private Headers headers;
    public final Form getForm() {
        return form;
    }
    public final Headers getHeaders() {
        return headers;
    }
    
    
}
