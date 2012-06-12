/**
 * copyright (a) 2010-2012 tomstools.org. All rights reserved.
 */
package org.tomstools.html.fetcher;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tomstools.common.log.Logger;
import org.tomstools.common.util.FileUtil;
import org.tomstools.common.util.Utils;
import org.tomstools.html.Util.HTMLUtil;
import org.tomstools.html.fetcher.thread.FetchTaskManager;

/**
 * @author lotomer
 * @date 2012-6-11
 * @time 下午02:09:32
 */
public class HTMLFetcherApp {
    private static final Logger logger = Logger.getLogger(HTMLFetcherApp.class);

    public static void main(String[] args) throws MalformedURLException {
        HTMLFetcherApp fetcher = new HTMLFetcherApp();
        if (fetcher.parseArgs(args)) {
            long start = System.currentTimeMillis();
            fetcher.fetchHTML();
            System.out.println("Total cost: " + (System.currentTimeMillis() - start) + "ms.");
        }
    }

    private String outpath; // 文件保存路径，默认保存在当前目录的html目录下
    private String regexp; // 待继续抓取的URL匹配该正则表达式的子页面
    private int threadCount; // 抓取深度。默认0，表示只抓取url指定的页面
    private URL url; // 带抓取的页面地址
    private String webRoot; // 页面主机地址，不包含path信息
    // private String charset;
    private int count;
    private String regexpFilterInclude;
    private String regexpFilterExclude;
    
    public HTMLFetcherApp() {
        threadCount = 1;
        count = 0;
        outpath = "html";
    }

    public boolean parseArgs(String[] args) throws MalformedURLException {
        if (args.length < 1) {
            System.out.println("wrong number of arguments");
            printHelp();
            return false;
        }
        for (int i = 0; i < args.length; i++) {
            if ("-out".equalsIgnoreCase(args[i])) {
                if (++i != args.length) {
                    outpath = args[i];
                } else {
                    printHelp();
                    return false;
                }
            } else if ("-regexp".equalsIgnoreCase(args[i])) {
                if (++i != args.length) {
                    regexp = args[i];
                } else {
                    printHelp();
                    return false;
                }
            }
            // else if ("-charset".equalsIgnoreCase(args[i])) {
            // if (++i != args.length) {
            // charset = args[i];
            // } else {
            // printHelp();
            // return false;
            // }
            // }
            else if ("-threadCount".equalsIgnoreCase(args[i])) {
                if (++i != args.length) {
                    try {
                        threadCount = Integer.valueOf(args[i]);
                        continue;
                    } catch (NumberFormatException e) {
                        System.out.println(e.getMessage());
                    }
                }
                printHelp();
                return false;
            } else if ("-regexpFilter:include".equalsIgnoreCase(args[i])) {
                if (++i != args.length) {
                    regexpFilterInclude = args[i];
                } else {
                    printHelp();
                    return false;
                }
            } else if ("-regexpFilter:exclude".equalsIgnoreCase(args[i])) {
                if (++i != args.length) {
                    regexpFilterExclude = args[i];
                } else {
                    printHelp();
                    return false;
                }
            } else if ("-h".equalsIgnoreCase(args[i])) {
                printHelp();
                return false;
            } else {
                url = new URL(args[i]);
                if (-1 < url.getPort()) {
                    webRoot = url.getProtocol() + "://" + url.getHost() + ":" + url.getPort();
                } else {
                    webRoot = url.getProtocol() + "://" + url.getHost();
                }
            }
        }

        return true;
    }

    /**
     * 抓取页面
     */
    public void fetchHTML() {
        if (Utils.isEmpty(url)) {
            printHelp();
            return;
        }
        logger.info("web root:" + webRoot);
        if (Utils.isEmpty(webRoot)) {
            System.out.println("invalid url.");
            printHelp();
            return;
        }
        HTMLFetcher fetcher = new HTMLFetcher();
        if (!fetcher.login(url)){
            return;
        }
        String htmlContent = fetcher.fetchHTMLContent(url.toString(), null, null);
        count++;
        String fileName = getFileName(url.getPath());
        if (Utils.isEmpty(fileName)) {
            fileName = String.valueOf(count);
        }
        FileUtil.saveFile(outpath + File.separator + fileName, htmlContent, null);

        // 根据正则表达式获取子页面
        if (!Utils.isEmpty(regexp)) {
            String subUrl = null;
            StringBuilder realRegexp = new StringBuilder();
            realRegexp.append("<a .*?href=\"(");
            realRegexp.append(regexp);
            realRegexp.append(")\".*?>(.*?)</a>");

            Pattern pattern = Pattern.compile(realRegexp.toString(), Pattern.CASE_INSENSITIVE
                    | Pattern.UNICODE_CASE | Pattern.DOTALL);
            Matcher matcher = pattern.matcher(htmlContent);
            FetchTaskManager taskMgr = new FetchTaskManager(fetcher,threadCount);
            while (matcher.find()) {
                count++;
                subUrl = HTMLUtil.getRealUrl(matcher.group(1), webRoot, url.getPath());
                fileName = matcher.group(2);
                if (Utils.isEmpty(fileName)) {
                    fileName = String.valueOf(count);
                }

                taskMgr.addTask(outpath + File.separator + "sub" + File.separator + fileName,
                        subUrl, regexpFilterInclude, regexpFilterExclude);
            }
            taskMgr.runTasks();
        }
    }

    private String getFileName(String url2) {
        if (url2.endsWith("/")) {
            url2 = url2.substring(0, url2.length() - 1);
        }
        int index = url2.lastIndexOf("/");
        if (-1 < index) {
            return url2.substring(index + 1);
        } else {
            return null;
        }
    }

    private void printHelp() {
        System.out.println("Usage: HTMLFetcher [options] url");
        System.out.println("Options are:");
        System.out
                .println("    -out                   outpath        The path where the fetched pages will be saved.");
        System.out
                .println("    -regexp                regexp         Matching the regular expression's child pages will be fetched too.");
        System.out
                .println("    -threadCount           threadCount    The count of thread will start. The default value is 1.");
        System.out
                .println("    -regexpFilter:include  regexpFilter   The regexp filter for the subpage's content witch will be included.");
        System.out
                .println("    -regexpFilter:exclude  regexpFilter   The regexp filter for the subpage's content witch will not be included.");
        System.out.println("    -h                     help           Print the help message.");
    }
}
