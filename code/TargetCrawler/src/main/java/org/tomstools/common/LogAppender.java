/**
 * copyright (a) 2010-2011 tomstools.org. All rights reserved.
 */
package org.tomstools.common;

import org.apache.log4j.RollingFileAppender;

/**
 * 日志输出工具
 * @author lotomer
 * @date 2011-12-14 
 * @time 下午02:39:01
 */
public class LogAppender extends RollingFileAppender{
    public void setFile(String file)
    {
        String fileName = null;
        if (null != file && !"".equals(file))
        {
            fileName = parseFileName(file);
        }else{
            fileName = "log4j.log";
        }

        super.setFile(fileName);
    }

    private String parseFileName(String file)
    {
        int beginIndex = file.indexOf("%{");
        if (-1 == beginIndex)
        {
            return file;
        }

        String preStr = file.substring(0, beginIndex);
        String tmpFile = file.substring(beginIndex + 2);
        int endIndex = tmpFile.indexOf("}");
        if (-1 == endIndex)
        {
            return file;
        }
        // 余下的字符串
        String postStr = tmpFile.substring(endIndex + 1);

        // 有变量
        String variableName = tmpFile.substring(0, endIndex).trim();
        if ("".equals(variableName))
        {
            // 变量名为空
            return file.substring(0, endIndex) + parseFileName(postStr);
        }

        // 变量名有效
        String variableValue = System.getProperty(variableName);
        if (null == variableValue)
        {
            return preStr + variableName + parseFileName(postStr);
        }
        else
        {
            return preStr + variableValue + parseFileName(postStr);
        }
    }
}
