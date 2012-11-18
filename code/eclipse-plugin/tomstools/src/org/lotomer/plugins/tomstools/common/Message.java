/**
 * 
 */
package org.lotomer.plugins.tomstools.common;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author lotomer
 *         国际化
 */
public final class Message
{
    private Message(){};
    public static String getString(final String className, final String key)
    {
        String value = "";
        try
        {
            value = ResourceBundle.getBundle(className).getString(key);
        }
        catch (MissingResourceException e)
        {
            value = "!" + key + "!";
        }
        
        return value;
    }
    
    public static String getString(final String key)
    {
        return getString("org.lotomer.plugins.tomstools.common.common",key);
    }
}
