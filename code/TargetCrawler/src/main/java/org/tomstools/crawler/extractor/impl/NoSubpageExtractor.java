/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.extractor.impl;

import java.util.Collections;
import java.util.List;

import org.tomstools.crawler.common.Element;
import org.tomstools.crawler.extractor.SubpageExtractor;

/**
 * 子页面抽取器：没有子页面的抽取器
 * 
 * @author admin
 * @date 2014年3月15日
 * @time 上午11:11:48
 * @version 1.0
 */
public class NoSubpageExtractor implements SubpageExtractor {
    public List<String> getSubpageUrls(Element element) {
        return Collections.emptyList();
    }
}
