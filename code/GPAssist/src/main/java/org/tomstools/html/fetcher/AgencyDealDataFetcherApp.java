/**
 * copyright (a) 2010-2012 tomstools.org. All rights reserved.
 */
package org.tomstools.html.fetcher;

import java.net.MalformedURLException;
import java.util.Calendar;

import org.tomstools.common.log.Logger;
import org.tomstools.common.util.Utils;
import org.tomstools.html.data.AgencyDeal;
import org.tomstools.html.data.AgencyDealDAO;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 机构交易数据获取
 * 
 * @author lotomer
 * @date 2012-6-11
 * @time 下午02:09:32
 */
public class AgencyDealDataFetcherApp {
    private static final Logger logger = Logger.getLogger(AgencyDealDataFetcherApp.class);

    /**
     * 获取指定机构指定日期的交易数据
     * 
     * @param agencySymbol 机构标识
     * @param date 指定日期
     */
    public void fetchAgencyDealDataByDate(String agencySymbol, String date) {
        if (!Utils.isEmpty(date)) {
            fetchAgencyDealData(agencySymbol, Integer.valueOf(date.substring(5, 7)), date);
        } else {
            logger.warn("date cannot be empty!");
        }
    }

    /**
     * 获取指定机构指定月份的交易数据
     * 
     * @param agencySymbol 结构标识
     * @param month 指定月份
     */
    public void fetchAgencyDealDataByMonth(String agencySymbol, String month) {
        if (!Utils.isEmpty(month)) {
            // 指定了月份，则使用月份
            fetchAgencyDealData(agencySymbol, Integer.valueOf(month), null);
        } else {
            // 没有指定月份，则取今年所有月份
            int currMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
            for (int i = 1; i <= currMonth; i++) {
                fetchAgencyDealData(agencySymbol, i, null);
            }
        }
    }

    private AgencyDealDAO agencyDeal;
    private HTMLFetcher fetcher;

    public AgencyDealDataFetcherApp() {
        fetcher = new HTMLFetcher();
    }

    public AgencyDealDataFetcherApp(String proxyHost, int proxyPort, String proxyScheme) {
        super();
        if (Utils.isEmpty(proxyHost)) {
            fetcher = new HTMLFetcher();
        } else {
            fetcher = new HTMLFetcher(proxyHost, proxyPort, proxyScheme);
        }
    }

    /**
     * 获取指定机构指定日期的交易数据
     * 
     * @param agencySymbol 机构标识，不能为null或空字符串
     * @param month 月份，1-12，不能为null
     * @param date 日期，可以为null
     */
    public void fetchAgencyDealData(String agencySymbol, int month, String date) {
        if (Utils.isEmpty(agencySymbol)) {
            logger.warn("The agencySymbol cannot be null or empty!");
            return;
        }
        // 第三步 查询主力的交易明细
        // http://quotes.money.163.com/hs/marketdata/service/jglhb.php?host=/hs/marketdata/service/jglhb.php&page=0&query=agencysymbol:80138252;date:8&fields=NO,SYMBOL,SNAME,TDATE,SMEBTCOMPANY4,SMEBTCOMPANY5,SMEBTCOMPANY1&sort=TDATE&order=desc&count=25&type=query&req=11526
        String urlStep3 = "http://quotes.money.163.com/hs/marketdata/service/jglhb.php?host=/hs/marketdata/service/jglhb.php&page=0&"
                + "query=agencysymbol:%s;date:%s&fields=NO,SYMBOL,SNAME,TDATE,SMEBTCOMPANY4,SMEBTCOMPANY5,SMEBTCOMPANY1&sort=TDATE&order=desc&count=2500&type=query&req=11526";

        String htmlContent = fetcher.fetchHTMLContent(String.format(urlStep3, agencySymbol, month));
        // System.out.println(htmlContent);
        if (Utils.isEmpty(htmlContent)) {
            logger.info("no content!");
            return;
        }
        JSONObject obj = (JSONObject) JSONObject.parse(htmlContent);
        logger.info("total:" + obj.get("total"));
        JSONArray arr = obj.getJSONArray("list");
        agencyDeal = new AgencyDealDAO();
        for (int i = 0; i < arr.size(); ++i) {
            JSONObject o = (JSONObject) arr.get(i);
            if (!Utils.isEmpty(date)) {
                // 指定了日期，则只获取该日期的数据
                if (date.equals(o.get("TDATE"))) {
                    agencyDeal.add(new AgencyDeal(agencySymbol, o.get("SYMBOL").toString(), o.get(
                            "SNAME").toString(), o.get("TDATE").toString(), o.get("SMEBTCOMPANY4")
                            .toString(), o.get("SMEBTCOMPANY5").toString()));
                }
            } else {
                agencyDeal.add(new AgencyDeal(agencySymbol, o.get("SYMBOL").toString(), o.get(
                        "SNAME").toString(), o.get("TDATE").toString(), o.get("SMEBTCOMPANY4")
                        .toString(), o.get("SMEBTCOMPANY5").toString()));
            }
        }

        agencyDeal.save();
    }

    private static void printHelp() {
        System.out.println("Usage: AgencyDealDataFetcherApp [options] agencySymbol");
        System.out.println("Options are:");
        System.out.println("    -m       month       1 to 12.");
        System.out.println("    -d       date        yyyy-MM-dd. Like 2013-09-01.");
        System.out.println("    -host    proxyHost   The proxy host.");
        System.out.println("    -port    proxyPort   The proxy port. Default is 8087");
        System.out.println("    -scheme  proxyScheme The proxy scheme. Default is http.");
        System.out.println("    -h       help        Print the help message.");
    }

    public static void main(String[] args) throws MalformedURLException {
        String agencySymbol = null;// "80138252";
        String month = null;// "9";
        String date = null;// "2013-09-06";
        String proxyHost = null;// "127.0.0.1";
        int proxyPort = 8087;
        String proxyScheme = "http";
        for (int i = 0; i < args.length; ++i) {
            if ("-m".equals(args[i])) {
                if (++i == args.length) {
                    System.err.println("-m month. 1 to 12");
                    System.exit(-1);
                } else {
                    month = args[i];
                }
            } else if ("-d".equals(args[i])) {
                if (++i == args.length) {
                    System.err.println("-d date. yyyy-MM-dd. Like 2013-09-01");
                    System.exit(-1);
                } else {
                    date = args[i];
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
            } else {
                agencySymbol = args[i];
            }
        }
        if (Utils.isEmpty(agencySymbol)) {
            System.err.println("The agencySymbol cannot be empty!");
            System.exit(-1);
        }

        AgencyDealDataFetcherApp fetcher = new AgencyDealDataFetcherApp(proxyHost, proxyPort,
                proxyScheme);
        long start = System.currentTimeMillis();
        if (!Utils.isEmpty(date)) {
            // 指定的具体的日期
            fetcher.fetchAgencyDealDataByDate(agencySymbol, date);
        } else {
            // 使用月份
            fetcher.fetchAgencyDealDataByMonth(agencySymbol, month);
        }

        System.out.println("Total cost: " + (System.currentTimeMillis() - start) + "ms.");
    }

}
