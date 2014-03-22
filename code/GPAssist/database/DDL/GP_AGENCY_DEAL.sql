
DROP TABLE IF EXISTS `GP_AGENCY_DEAL`;
CREATE TABLE `GP_AGENCY_DEAL` (
  `SYMBOL` varchar(32) NOT NULL COMMENT '股票代码',
  `SNAME` varchar(128) NOT NULL COMMENT '股票名称',
  `TDATE` varchar(32) NOT NULL COMMENT '日期',
  `BUY` bigint(20) DEFAULT NULL COMMENT '买入',
  `SALE` bigint(20) DEFAULT NULL COMMENT '卖出',
  `AGENCY_SYMBOL` varchar(32) NOT NULL COMMENT '机构标识',
  `IN_TIME` datetime DEFAULT NULL COMMENT '入库时间',
  KEY `IDX_DEAL_TDATE` (`TDATE`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='机构交易数据';