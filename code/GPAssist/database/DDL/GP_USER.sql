
DROP TABLE IF EXISTS `gp_user`;
CREATE TABLE `gp_user` (
  `USER` varchar(32) NOT NULL COMMENT '用户名',
  `PASSWORD` varchar(64) NOT NULL,
  `BEGIN_TIME` datetime DEFAULT NULL COMMENT '注册时间',
  `END_TIME` datetime DEFAULT NULL COMMENT '失效时间'
) ENGINE=MyISAM DEFAULT CHARSET=utf8;


