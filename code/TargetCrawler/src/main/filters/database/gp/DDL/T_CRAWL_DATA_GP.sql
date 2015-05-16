CREATE TABLE `T_CRAWL_DATA_GP` (
  `ID` int(11) NOT NULL auto_increment COMMENT '编号',
  `CRAWL_ID` int(11) NOT NULL COMMENT '编号，对应T_CRAWL_STATUS中的ID',
  `URL_PREFIX` varchar(128) NOT NULL COMMENT '网站',
  `URL` varchar(256) NOT NULL COMMENT '网站URL地址',
  `TITLE` varchar(1024) NOT NULL COMMENT '标题',
  `IN_TIME` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`ID`),
  KEY `CRAWL_D_GP_ID` (`CRAWL_ID`),
  KEY `CRAWL_D_GP_TIME` (`IN_TIME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='爬取状态表';