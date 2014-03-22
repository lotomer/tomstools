package org.tomstools.crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.tomstools.crawler.config.CrawlingRule;
import org.tomstools.crawler.config.Target;
import org.tomstools.crawler.dao.FileResultDAO;
import org.tomstools.crawler.example.CaipiaoDaLeTou;
import org.tomstools.crawler.example.CaipiaoShuangSeQiu;
import org.tomstools.crawler.example.EasymoneyNewsGN;
import org.tomstools.crawler.example.ESF0731fdc;
import org.tomstools.crawler.example.HuNanGuoTuZiYuanCaiKuangZhuanRang;
import org.tomstools.crawler.example.Floor0731fdc;
import org.xml.sax.SAXException;

public class TargetCrawlerApp {

    public static void main(String[] args) throws ParserConfigurationException, SAXException,
            IOException, InterruptedException {
        String separator = "\t";
        String rootDir = "e:/work";
        if (0 < args.length){
            rootDir = args[0];
            if (1 < args.length){
                separator = args[1];
            }
        }
        
        FileResultDAO resultDAO = new FileResultDAO(rootDir, separator, new String[] { "\r", "\n",
                separator });
        
        CrawlingRule crawlingRule = new CrawlingRule();
        crawlingRule.setBatchIntervalInit(100);
        crawlingRule.setBatchIntervalFinal(5000);
        crawlingRule.setBatchIntervalStep(500);
        crawlingRule.setBatchSizeInit(50);
        crawlingRule.setBatchSizeFinal(30);
        crawlingRule.setBatchSizeStep(1);
        
        List<Target> targets = getTargets(crawlingRule);
        
        // 开始运行
        TargetCrawler targetCrawler = new TargetCrawler(targets, resultDAO);
        targetCrawler.run();
    }

    private static List<Target> getTargets(CrawlingRule crawlingRule) {
        return getMyTargets(crawlingRule);
    }
    
    protected static List<Target> getMyTargets(CrawlingRule crawlingRule) {
        List<Target> targets = new ArrayList<Target>();

        // 彩票-双色球
//        targets.add(new CaipiaoShuangSeQiu(crawlingRule));
//
//        // 彩票-大乐透
//        targets.add(new CaipiaoDaLeTou(crawlingRule));
//
//        // 湖南国土资源厅-采矿转让信息
//        targets.add(new HuNanGuoTuZiYuanCaiKuangZhuanRang(crawlingRule));
//
//        // 东方财富-国内经济
//        targets.add(new EasymoneyNewsGN(crawlingRule));
//
//        // 0731fds新房房源
        targets.add(new Floor0731fdc(crawlingRule));
        
        // 0731fds二手房房源
        targets.add(new ESF0731fdc(crawlingRule));

        return targets;
    }
    
    protected static List<Target> getAllTargets(CrawlingRule crawlingRule) {
        List<Target> targets = new ArrayList<Target>();
        // 彩票-双色球
        targets.add(new CaipiaoShuangSeQiu(crawlingRule));

        // 彩票-大乐透
        targets.add(new CaipiaoDaLeTou(crawlingRule));

        // 湖南国土资源厅-采矿转让信息
        targets.add(new HuNanGuoTuZiYuanCaiKuangZhuanRang(crawlingRule));

        // 东方财富-国内经济
        targets.add(new EasymoneyNewsGN(crawlingRule));

        // 0731fds新房房源
        targets.add(new Floor0731fdc(crawlingRule));
        
        // 0731fds二手房房源
        targets.add(new ESF0731fdc(crawlingRule));

        return targets;
    }
}
