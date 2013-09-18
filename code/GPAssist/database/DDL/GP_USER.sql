
DROP TABLE IF EXISTS `gp_user`;
CREATE TABLE `gp_user` (
  `USER` varchar(32) NOT NULL COMMENT '登录名',
  `PASSWORD` varchar(64) NOT NULL,
  `NAME` varchar(32) NOT NULL COMMENT '用户名称',
  `BEGIN_TIME` datetime DEFAULT NULL COMMENT '注册时间',
  `END_TIME` datetime DEFAULT NULL COMMENT '失效时间',
  `IS_VALID` char(1) DEFAULT '1' COMMENT '是否有效。1 有效 0 无效；默认有效'
) ENGINE=MyISAM DEFAULT CHARSET=utf8;


