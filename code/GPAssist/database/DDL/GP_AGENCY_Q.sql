
DROP TABLE IF EXISTS `GP_AGENCY_Q`;
CREATE TABLE `GP_AGENCY_Q` (
  `AGENCY_SYMBOL` varchar(32) DEFAULT NULL COMMENT '机构标识',
  `SNAME` varchar(128) NOT NULL COMMENT '机构名称',
  `ORDER_NUM` int(11) NOT NULL DEFAULT '0',
  `OWNER` varchar(32) DEFAULT 'ADMIN',
  KEY `IDX_Q_AGENCY_SYMBOL` (`AGENCY_SYMBOL`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='查询用的机构信息';
