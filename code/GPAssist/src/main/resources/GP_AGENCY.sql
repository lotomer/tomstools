CREATE TABLE `GP_AGENCY_DEAL` (
  `SYMBOL` varchar(10) NOT NULL COMMENT '股票代码',
  `SNAME` varchar(128) NOT NULL COMMENT '股票名称',
  `TDATE` varchar(32) NOT NULL COMMENT '日期',
  `BUY` float DEFAULT NULL COMMENT '买入',
  `SALE` float DEFAULT NULL COMMENT '卖出',
  `AGENCY_SYMBOL` varchar(10) NOT NULL COMMENT '机构标识',
  UNIQUE KEY `UNI_SNAME` (`SNAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='机构交易数据';