/**
 * copyright (a) 2010-2012 tomstools.org. All rights reserved.
 */
package org.tomstools.html.fetcher.thread;

import java.io.File;

import org.tomstools.common.log.Logger;
import org.tomstools.common.util.FileUtil;
import org.tomstools.html.fetcher.HTMLFetcher;

/**
 * @author lotomer
 * @date 2012-6-11
 * @time 下午03:56:10
 */
public class FetchTask {
    private static final Logger logger = Logger.getLogger(FetchTask.class);
    private String outputFileName;
    private String url;
    private String regexpFilterInclude;
    private String regexpFilterExclude;
    private HTMLFetcher fetcher;

    public FetchTask(HTMLFetcher fetcher, String outputFileName, String url, String regexpFilterInclude, String regexpFilterExclude) {
        this.fetcher = fetcher;
        this.outputFileName = outputFileName;
        this.url = url;
        this.regexpFilterInclude = regexpFilterInclude;
        this.regexpFilterExclude = regexpFilterExclude;
    }

    public void run() {
        logger.info("run task: " + this);
        String htmlContent = fetcher.fetchHTMLContent(url, regexpFilterInclude,regexpFilterExclude);
        outputFileName = outputFileName + "." + FileUtil.getFileExt(url);
        //String charset = HTMLUtil.parseCharset(htmlContent);
        FileUtil.saveFile(new File(outputFileName), htmlContent,null);
    }

    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append("task:{url:");
        msg.append(url);
        msg.append(",outputFileName:");
        msg.append(outputFileName);
        msg.append(",regexpFilterInclude:");
        msg.append(regexpFilterInclude);
        msg.append(",regexpFilterExclude:");
        msg.append(regexpFilterExclude);
        msg.append("}");
        return msg.toString();
    }
}
