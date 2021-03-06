
CREATE TABLE `T_CRAWL_STATUS` (
  `ID` int(11) NOT NULL auto_increment COMMENT '编号',
  `NAME` varchar(64) NOT NULL COMMENT '爬取名称',
  `SITE_NAME` varchar(64) DEFAULT NULL COMMENT '网站名称',
  `CHANNEL_NAME` varchar(64) DEFAULT NULL COMMENT '栏目名称',
  `URL` varchar(256) NOT NULL COMMENT '网站URL地址',
  `STATUS` varchar(1024) NOT NULL COMMENT '最新状态',
  `UPDATE_TIME` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`ID`),
  KEY `CRAWL_URL` (`URL`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='爬取状态表';
