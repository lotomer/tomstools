/**
 * copyright (a) 2010-2012 tomstools.org. All rights reserved.
 */
package org.tomstools.html.fetcher;

import java.net.MalformedURLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tomstools.common.util.Utils;
import org.tomstools.html.Util.HTMLUtil;
import org.tomstools.html.data.Agency;
import org.tomstools.html.data.AgencyDAO;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @author lotomer
 * @date 2012-6-11
 * @time 下午02:09:32
 */
public class AgencyFetcherApp {
    // private static final Logger logger =
    // Logger.getLogger(AgencyFetcherApp.class);

    private AgencyDAO agency;
    private HTMLFetcher fetcher;

    public AgencyFetcherApp() {
        fetcher = new HTMLFetcher();
    }

    public AgencyFetcherApp(String proxyHost, int proxyPort, String proxyScheme) {
        if (Utils.isEmpty(proxyHost)) {
            fetcher = new HTMLFetcher();
        } else {
            fetcher = new HTMLFetcher(proxyHost, proxyPort, proxyScheme);
        }
    }

    /**
     * 抓取页面
     */
    public void fetchAgency(String beginDate, String endDate) {
        // 第一步 获取每日股票龙虎榜数据
        // http://quotes.money.163.com/hs/marketdata/service/lhb.php?host=/hs/marketdata/service/lhb.php&page=0&query=start:2013-09-06;end:2013-09-06&fields=NO,SYMBOL,SNAME,TDATE,TCLOSE,PCHG,SMEBTSTOCK1,SYMBOL,VOTURNOVER,COMPAREA,VATURNOVER,SYMBOL&sort=TDATE&order=desc&count=150&type=query&initData=[object%20Object]&req=11511
        String urlStep1 = "http://quotes.money.163.com/hs/marketdata/service/lhb.php?host=/hs/marketdata/service/lhb.php&page=0&"
                + "query=start:%s;end:%s&fields=NO,SYMBOL,TDATE,SNAME,SMEBTSTOCK1&count=2500&type=query&req=11511";
        // 第二步 逐个获取龙虎榜股票涉及的主力
        // http://quotes.money.163.com/hs/marketdata/mrlhbSub.php?clear=1202&symbol=000663&type=01&date=2013-09-06&width=920&height=500&modal=true&frame=true
        String urlStep2 = "http://quotes.money.163.com/hs/marketdata/mrlhbSub.php?clear=1202&"
                + "symbol=%s&type=%s&date=%s&width=920&height=500&modal=true";
        // 第三步 查询主力的交易明细
        // http://quotes.money.163.com/hs/marketdata/service/jglhb.php?host=/hs/marketdata/service/jglhb.php&page=0&query=agencysymbol:80138252;date:8&fields=NO,SYMBOL,SNAME,TDATE,SMEBTCOMPANY4,SMEBTCOMPANY5,SMEBTCOMPANY1&sort=TDATE&order=desc&count=25&type=query&req=11526
        // String urlStep3 =
        // "http://quotes.money.163.com/hs/marketdata/service/jglhb.php?host=/hs/marketdata/service/jglhb.php&page=0&query=agencysymbol:80138252;date:8&fields=NO,SYMBOL,SNAME,TDATE,SMEBTCOMPANY4,SMEBTCOMPANY5,SMEBTCOMPANY1&sort=TDATE&order=desc&count=250&type=query&req=11526";

        String htmlContent = fetcher.fetchHTMLContent(String.format(urlStep1, beginDate, endDate));
        System.out.println(htmlContent);
        if (Utils.isEmpty(htmlContent)) {
            return;
        }
        JSONObject obj = (JSONObject) JSONObject.parse(htmlContent);
        System.out.println("total:" + obj.get("total"));
        JSONArray arr = obj.getJSONArray("list");
        System.out.println(arr.size());
        String host = HTMLUtil.getHost(urlStep2);
        System.out.println(host);
        agency = new AgencyDAO();
        for (int i = 0; i < arr.size(); ++i) {
            JSONObject o = (JSONObject) arr.get(i);
            htmlContent = fetcher.fetchHTMLContent(String.format(urlStep2, o.get("SYMBOL"),o.get("SMEBTSTOCK11"),
                    o.get("TDATE")));
            // System.out.println(htmlContent);
            parseSubUrls(htmlContent,host);
             break;
        }

        agency.save();

    }

    private void parseSubUrls(String htmlContent,String host) {
        // 根据正则表达式获取子页面
        StringBuilder realRegexp = new StringBuilder();
        realRegexp.append("<a .*?href='(/marketdata/agencylist_([0-9]+).html )'.*?>(.*?)</a>");

        Pattern pattern = Pattern.compile(realRegexp.toString(), Pattern.CASE_INSENSITIVE
                | Pattern.UNICODE_CASE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(htmlContent);
        while (matcher.find()) {
            agency.addAgency(new Agency(matcher.group(2), matcher.group(3), host + matcher.group(1)));
        }
    }

    private static void printHelp() {
        System.out.println("Usage: AgencyDealDataFetcherApp [options] agencySymbol");
        System.out.println("Options are:");
        System.out.println("    -b       beginDate   yyyy-MM-dd. Like 2013-09-01.");
        System.out.println("    -e       endDate     yyyy-MM-dd. Like 2013-09-01.");
        System.out.println("    -host    proxyHost   The proxy host.");
        System.out.println("    -port    proxyPort   The proxy port. Default is 8087");
        System.out.println("    -scheme  proxyScheme The proxy scheme. Default is http.");
        System.out.println("    -h       help        Print the help message.");
    }

    public static void main(String[] args) throws MalformedURLException {
        String beginDate = null;// "2013-09-06";
        String endDate = null;// "2013-09-06";
        String proxyHost = null;// "127.0.0.1";
        int proxyPort = 8087;
        String proxyScheme = "http";
        for (int i = 0; i < args.length; ++i) {
            if ("-b".equals(args[i])) {
                if (++i == args.length) {
                    System.err.println("-b beginDate  yyyy-MM-dd. Like 2013-09-01");
                    System.exit(-1);
                } else {
                    beginDate = args[i];
                }
            } else if ("-e".equals(args[i])) {
                if (++i == args.length) {
                    System.err.println("-e endDate  yyyy-MM-dd. Like 2013-09-01");
                    System.exit(-1);
                } else {
                    endDate = args[i];
                }
            } else if ("-host".equals(args[i])) {
                if (++i == args.length) {
                    System.err.println("-host proxyHost. ");
                    System.exit(-1);
                } else {
                    proxyHost = args[i];
                }
            } else if ("-port".equals(args[i])) {
                if (++i == args.length) {
                    System.err.println("-port proxyPort. Default is 8087");
                    System.exit(-1);
                } else {
                    proxyPort = Integer.valueOf(args[i]);
                }
            } else if ("-scheme".equals(args[i])) {
                if (++i == args.length) {
                    System.err.println("-scheme proxyScheme. Default is http");
                    System.exit(-1);
                } else {
                    proxyScheme = args[i];
                }
            } else if ("-h".equals(args[i])) {
                printHelp();
            }
        }
        if (Utils.isEmpty(beginDate) && Utils.isEmpty(endDate)) {
            System.err.println("The beginDate and endDate cannot be both empty!");
            System.exit(-1);
        }
        AgencyFetcherApp fetcher = new AgencyFetcherApp(proxyHost, proxyPort, proxyScheme);
        long start = System.currentTimeMillis();
        if (Utils.isEmpty(beginDate)) {
            beginDate = endDate;
        }
        if (Utils.isEmpty(endDate)) {
            endDate = beginDate;
        }
        fetcher.fetchAgency(beginDate, endDate);

        System.out.println("Total cost: " + (System.currentTimeMillis() - start) + "ms.");
    }
}
