/**
 * copyright (a) 2010-2012 tomstools.org. All rights reserved.
 */
package org.tomstools.html.fetcher;

import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tomstools.common.log.Logger;
import org.tomstools.common.util.MD5;
import org.tomstools.common.util.Utils;
import org.tomstools.html.Util.HTMLUtil;
import org.tomstools.html.data.Agency;
import org.tomstools.html.data.AgencyDAO;
import org.tomstools.html.data.AgencyDeal;
import org.tomstools.html.data.AgencyDealDAO;
import org.tomstools.html.data.StockDeal;
import org.tomstools.html.data.StockDealDAO;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @author lotomer
 * @date 2012-6-11
 * @time 下午02:09:32
 */
public class AgencyFetcherApp {
     private static final Logger LOG =
     Logger.getLogger(AgencyFetcherApp.class);

    private AgencyDAO agency;
    private HTMLFetcher fetcher;

    private AgencyDealDAO agencyDeal;

    private MD5 md5;

    public AgencyFetcherApp() throws NoSuchAlgorithmException {
        md5 = new MD5();
        fetcher = new HTMLFetcher();
    }

    public AgencyFetcherApp(String proxyHost, int proxyPort, String proxyScheme) throws NoSuchAlgorithmException {
        if (Utils.isEmpty(proxyHost)) {
            fetcher = new HTMLFetcher();
        } else {
            fetcher = new HTMLFetcher(proxyHost, proxyPort, proxyScheme);
        }
        md5 = new MD5();
    }

    /**
     * 抓取页面
     */
    public void fetchAgency(String beginDate, String endDate) {
        // 第一步 获取每日股票龙虎榜数据
        // http://quotes.money.163.com/hs/marketdata/service/lhb.php?host=/hs/marketdata/service/lhb.php&page=0&query=start:2013-09-06;end:2013-09-06&fields=NO,SYMBOL,SNAME,TDATE,TCLOSE,PCHG,SMEBTSTOCK1,SYMBOL,VOTURNOVER,COMPAREA,VATURNOVER,SYMBOL&sort=TDATE&order=desc&count=150&type=query&initData=[object%20Object]&req=11511
        String urlStep1 = "http://quotes.money.163.com/hs/marketdata/service/lhb.php?host=/hs/marketdata/service/lhb.php&page=0&"
                + "query=start:%s;end:%s&fields=NO,SYMBOL,TDATE,SNAME,SMEBTSTOCK1&sort=TDATE&order=desc&count=250000&type=query&req=11511";
        // 第二步 逐个获取龙虎榜股票涉及的主力
        // http://quotes.money.163.com/hs/marketdata/mrlhbSub.php?clear=1202&symbol=000663&type=01&date=2013-09-06&width=920&height=500&modal=true&frame=true
        String urlStep2 = "http://quotes.money.163.com/hs/marketdata/mrlhbSub.php?clear=1202&"
                + "symbol=%s&type=%s&date=%s&width=920&height=500&modal=true";
        // 第三步 查询主力的交易明细
        // http://quotes.money.163.com/hs/marketdata/service/jglhb.php?host=/hs/marketdata/service/jglhb.php&page=0&query=agencysymbol:80138252;date:8&fields=NO,SYMBOL,SNAME,TDATE,SMEBTCOMPANY4,SMEBTCOMPANY5,SMEBTCOMPANY1&sort=TDATE&order=desc&count=25&type=query&req=11526
        // String urlStep3 =
        // "http://quotes.money.163.com/hs/marketdata/service/jglhb.php?host=/hs/marketdata/service/jglhb.php&page=0&query=agencysymbol:80138252;date:8&fields=NO,SYMBOL,SNAME,TDATE,SMEBTCOMPANY4,SMEBTCOMPANY5,SMEBTCOMPANY1&sort=TDATE&order=desc&count=250&type=query&req=11526";

        String htmlContent = fetcher.fetchHTMLContent(String.format(urlStep1, beginDate, endDate));
        //System.out.println(htmlContent);
        if (Utils.isEmpty(htmlContent)) {
            return;
        }
        JSONObject obj = (JSONObject) JSONObject.parse(htmlContent);
        LOG.info("total:" + obj.get("total"));
        JSONArray arr = obj.getJSONArray("list");
        LOG.info("fetch:"+arr.size());
        String host = HTMLUtil.getHost(urlStep2);
        LOG.info("host:"+host);
        agency = new AgencyDAO();
        agencyDeal = new AgencyDealDAO();
        StockDealDAO stockDeal = new StockDealDAO();
        for (int i = 0; i < arr.size(); ++i) {
            JSONObject o = (JSONObject) arr.get(i);
            htmlContent = fetcher.fetchHTMLContent(String.format(urlStep2, o.get("SYMBOL"),o.get("SMEBTSTOCK11"),
                    o.get("TDATE")));
            parseSubUrls(htmlContent,host,o.get("SYMBOL").toString(),o.get("SNAME").toString(),o.get("TDATE").toString());
            stockDeal.add(new StockDeal(o.get("SYMBOL").toString(), o.get("SNAME").toString(), o.get("TDATE").toString(), o.get("TCLOSE").toString(), o.get("PCHG").toString()));
        }

        agency.save();
        stockDeal.save();
        // 先删除指定日期的交易数据，然后再添加
        agencyDeal.clean(beginDate,endDate);
        agencyDeal.save();
    }
    
    Pattern pattern = Pattern.compile("异动期内买入金额最大的前5名</td></tr>(.*?)</tbody>", Pattern.CASE_INSENSITIVE
            | Pattern.UNICODE_CASE | Pattern.DOTALL);
    Pattern valuePattern = Pattern.compile("<tr>\\s*?<[/]{0,1}td>(.*?)</td>\\s*?<td>(.*?)</td>\\s*?<td>(.*?)</td>\\s*?<td>(.*?)</td>", Pattern.CASE_INSENSITIVE
            | Pattern.UNICODE_CASE | Pattern.DOTALL);
    Pattern namePattern = Pattern.compile("<a .*?href='(/marketdata/agencylist_(.*?).html )'.*?>(.*?)</a>", Pattern.CASE_INSENSITIVE
            | Pattern.UNICODE_CASE | Pattern.DOTALL);
    private void parseSubUrls(String htmlContent,String host,String symbol,String sname,String tdate) {
        Matcher matcher = pattern.matcher(htmlContent);
        if (matcher.find()) {
            // 获取正文
            Matcher valueMatcher = valuePattern.matcher(matcher.group(1));
            while (valueMatcher.find()) {
                // 循环获取值
                Matcher nameMatcher = namePattern.matcher(valueMatcher.group(1));
                if (nameMatcher.find()){
                    // 添加机构信息
                    agency.add(new Agency(nameMatcher.group(2),nameMatcher.group(3),host +nameMatcher.group(1)));
                    //添加交易数据
                    agencyDeal.add(new AgencyDeal(nameMatcher.group(2), symbol, sname, tdate, valueMatcher.group(2).trim(), valueMatcher.group(4).trim()));
                }else{
                 // 添加机构信息
                    String name = HTMLUtil.removeTags(valueMatcher.group(1));
                    agency.add(new Agency(md5.md5(name),name,""));
                    //添加交易数据
                    agencyDeal.add(new AgencyDeal(md5.md5(name), symbol, sname, tdate, valueMatcher.group(2).trim(), valueMatcher.group(4).trim()));
                    //System.out.println(HTMLUtil.removeTags(valueMatcher.group(1))+":"+valueMatcher.group(2).trim()+":"+valueMatcher.group(4).trim());
                }
            }
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

    public static void main(String[] args) throws MalformedURLException, NoSuchAlgorithmException {
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
        
        // for test begin
        //beginDate = "2013-01-01";
        //endDate = "2013-09-10";
        // for test end
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
