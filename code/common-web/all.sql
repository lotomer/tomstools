
SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `T_C_SITE`
-- ----------------------------
DROP TABLE IF EXISTS `T_C_SITE`;
CREATE TABLE `T_C_SITE` (
  `SITE_ID` int(11) NOT NULL COMMENT '站点编号',
  `SITE_NAME` varchar(64) NOT NULL COMMENT '站点名称',
  `SITE_URL` varchar(512) NOT NULL COMMENT '站点地址',
  `LANG_ID` int(11) DEFAULT NULL COMMENT '语言编号',
  `CRAWL_CODE` varchar(64) NOT NULL COMMENT '爬虫编码',
  `COUNTRY_CODE` int(11) DEFAULT NULL,
  `IS_VALID` char(1) NOT NULL DEFAULT '1' COMMENT '是否启用。0 不启用；1 启用。默认1',
  PRIMARY KEY (`SITE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='站点信息';

-- ----------------------------
-- Records of T_C_SITE
-- ----------------------------
INSERT INTO `T_C_SITE` VALUES ('1', '美联社', 'http://www.ap.org', '2', 'nutch', '2', '1');
INSERT INTO `T_C_SITE` VALUES ('2', '合众国际社', 'http://www.upi.com', '2', 'nutch', '2', '1');
INSERT INTO `T_C_SITE` VALUES ('3', '人民网', 'http://energy.people.com.cn/', '1', 'nutch', '1', '1');
INSERT INTO `T_C_SITE` VALUES ('4', '凤凰网', 'http://www.ifeng.com', '1', 'nutch', '1', '1');

-- ----------------------------
-- Table structure for `T_C_WORD`
-- ----------------------------
DROP TABLE IF EXISTS `T_C_WORD`;
CREATE TABLE `T_C_WORD` (
  `WORD_ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '词汇编号',
  `TYPE_ID` int(11) NOT NULL COMMENT '类别编号',
  `LANG_ID` int(11) NOT NULL COMMENT '语言编号',
  `WORD_CONTENT` varchar(128) NOT NULL COMMENT '词汇内容',
  PRIMARY KEY (`WORD_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='词汇信息表';

-- ----------------------------
-- Records of T_C_WORD
-- ----------------------------

-- ----------------------------
-- Table structure for `T_M_COUNTRY`
-- ----------------------------
DROP TABLE IF EXISTS `T_M_COUNTRY`;
CREATE TABLE `T_M_COUNTRY` (
  `COUNTRY_CODE` int(11) NOT NULL COMMENT '国家编码',
  `COUNTRY_NAME` varchar(128) NOT NULL COMMENT '国家名称',
  PRIMARY KEY (`COUNTRY_CODE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='国家信息';

-- ----------------------------
-- Records of T_M_COUNTRY
-- ----------------------------
INSERT INTO `T_M_COUNTRY` VALUES ('1', '中国');
INSERT INTO `T_M_COUNTRY` VALUES ('2', '美国');
INSERT INTO `T_M_COUNTRY` VALUES ('3', '英国');

-- ----------------------------
-- Table structure for `T_M_CRAWL`
-- ----------------------------
DROP TABLE IF EXISTS `T_M_CRAWL`;
CREATE TABLE `T_M_CRAWL` (
  `CRAWL_CODE` varchar(64) NOT NULL COMMENT '爬虫编码',
  `CRAWL_NAME` varchar(64) NOT NULL COMMENT '爬虫名称',
  `CRAWL_FREQUENCY` int(11) NOT NULL COMMENT '爬取频率',
  `IS_VALID` char(1) NOT NULL DEFAULT '1' COMMENT '是否启用。0 不启用；1 启用。默认1',
  PRIMARY KEY (`CRAWL_CODE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='爬虫设置';

-- ----------------------------
-- Records of T_M_CRAWL
-- ----------------------------
INSERT INTO `T_M_CRAWL` VALUES ('nutch', 'Nutch爬虫', '30', '1');

-- ----------------------------
-- Table structure for `T_M_DICT`
-- ----------------------------
DROP TABLE IF EXISTS `T_M_DICT`;
CREATE TABLE `T_M_DICT` (
  `TYPE` varchar(64) NOT NULL COMMENT '类别',
  `DICT_NUM` varchar(64) NOT NULL COMMENT '字段编号',
  `DICT_NAME` varchar(64) NOT NULL COMMENT '字段名称',
  `DICT_TITLE` varchar(64) NOT NULL COMMENT '字段描述',
  UNIQUE KEY `UNI_TYPE_NUM` (`TYPE`,`DICT_NUM`),
  UNIQUE KEY `UNI_TYPE_NAME` (`TYPE`,`DICT_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='字典表';

-- ----------------------------
-- Records of T_M_DICT
-- ----------------------------

-- ----------------------------
-- Table structure for `T_M_LANGUAGE`
-- ----------------------------
DROP TABLE IF EXISTS `T_M_LANGUAGE`;
CREATE TABLE `T_M_LANGUAGE` (
  `LANG_ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '语言编号',
  `LANG_NAME` varchar(32) NOT NULL COMMENT '语言名称',
  PRIMARY KEY (`LANG_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='语言';

-- ----------------------------
-- Records of T_M_LANGUAGE
-- ----------------------------
INSERT INTO `T_M_LANGUAGE` VALUES ('1', '中文');
INSERT INTO `T_M_LANGUAGE` VALUES ('2', '英文');

-- ----------------------------
-- Table structure for `T_M_MENUS`
-- ----------------------------
DROP TABLE IF EXISTS `T_M_MENUS`;
CREATE TABLE `T_M_MENUS` (
  `MENU_ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '菜单编号',
  `PAGE_ID` int(11) DEFAULT NULL COMMENT '页面编号',
  `MENU_NAME` varchar(64) NOT NULL COMMENT '菜单名称',
  `PARENT_ID` int(11) NOT NULL DEFAULT '-1' COMMENT '父编号。默认为-1，表示一级模块。',
  `IN_TIME` datetime DEFAULT NULL COMMENT '生成时间',
  `ICON_CLASS` varchar(32) DEFAULT NULL COMMENT '图标样式类。为空表示不指定图标',
  `ORDER_NUM` int(11) NOT NULL DEFAULT '0' COMMENT '显示顺序',
  `IS_SHOW` char(1) NOT NULL DEFAULT '0' COMMENT '是否显示。可选值：0 不显示；1 显示',
  `IS_VALID` char(1) NOT NULL DEFAULT '1' COMMENT '是否有效。1 有效；0 无效。默认1',
  PRIMARY KEY (`MENU_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=105007 DEFAULT CHARSET=utf8 COMMENT='菜单配置表';

-- ----------------------------
-- Records of T_M_MENUS
-- ----------------------------
INSERT INTO `T_M_MENUS` VALUES ('101', '101', '系统首页', '-1', null, 'icon-tip', '1', '1', '1');
INSERT INTO `T_M_MENUS` VALUES ('102', '102', '舆情查询', '-1', null, 'icon-search', '2', '0', '1');
INSERT INTO `T_M_MENUS` VALUES ('103', '103', '舆情报表', '-1', null, 'icon-search', '3', '0', '1');
INSERT INTO `T_M_MENUS` VALUES ('104', '104', '舆情设置', '-1', null, null, '4', '0', '1');
INSERT INTO `T_M_MENUS` VALUES ('105', '105', '系统管理', '-1', null, null, '5', '0', '1');
INSERT INTO `T_M_MENUS` VALUES ('102001', '102001', '仪表盘', '102', null, null, '1', '0', '1');
INSERT INTO `T_M_MENUS` VALUES ('102002', '102002', '舆情查询', '102', null, null, '2', '0', '1');
INSERT INTO `T_M_MENUS` VALUES ('102003', '102003', '预警信息查询', '102', null, null, '3', '0', '1');
INSERT INTO `T_M_MENUS` VALUES ('102004', '102004', '月周报', '102', null, null, '4', '0', '1');
INSERT INTO `T_M_MENUS` VALUES ('103001', '103001', '舆情信息条数统计', '103', null, null, '1', '0', '1');
INSERT INTO `T_M_MENUS` VALUES ('103002', '103002', '倾向性条数统计', '103', null, null, '2', '0', '1');
INSERT INTO `T_M_MENUS` VALUES ('103003', '103003', '趋势统计', '103', null, null, '3', '0', '1');
INSERT INTO `T_M_MENUS` VALUES ('103004', '103004', '词汇频次图', '103', null, null, '4', '0', '1');
INSERT INTO `T_M_MENUS` VALUES ('104001', '104001', '爬虫设置', '104', null, null, '1', '0', '1');
INSERT INTO `T_M_MENUS` VALUES ('104002', '104002', '监控站点设置', '104', null, null, '2', '0', '1');
INSERT INTO `T_M_MENUS` VALUES ('104003', '104003', '智能词条管理', '104', null, null, '3', '0', '1');
INSERT INTO `T_M_MENUS` VALUES ('104004', '104004', '词汇管理', '104', null, null, '4', '0', '1');
INSERT INTO `T_M_MENUS` VALUES ('104005', '104005', '预警设置', '104', null, null, '5', '0', '1');
INSERT INTO `T_M_MENUS` VALUES ('105001', '105001', '菜单管理', '105', null, null, '1', '0', '1');
INSERT INTO `T_M_MENUS` VALUES ('105002', '105002', '角色管理', '105', null, null, '2', '0', '1');
INSERT INTO `T_M_MENUS` VALUES ('105003', '105003', '用户管理', '105', null, null, '3', '0', '1');
INSERT INTO `T_M_MENUS` VALUES ('105004', '105004', '授权管理', '105', null, null, '4', '0', '1');
INSERT INTO `T_M_MENUS` VALUES ('105005', '105005', '用户信息', '105', null, null, '5', '0', '1');
INSERT INTO `T_M_MENUS` VALUES ('105006', '105006', '访问设置', '105', null, null, '6', '0', '1');

-- ----------------------------
-- Table structure for `T_M_PAGES`
-- ----------------------------
DROP TABLE IF EXISTS `T_M_PAGES`;
CREATE TABLE `T_M_PAGES` (
  `PAGE_ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '页面编号',
  `PAGE_NAME` varchar(64) NOT NULL COMMENT '页面名称',
  `CONTENT_URL` varchar(512) DEFAULT NULL COMMENT '内容对应的URL',
  `PARAMS` varchar(512) DEFAULT NULL COMMENT '页面参数',
  `IN_TIME` datetime DEFAULT NULL COMMENT '生成时间',
  `IS_VALID` char(1) NOT NULL DEFAULT '1' COMMENT '是否有效。1 有效；0 无效。默认1',
  `WIDTH` int(11) DEFAULT '1200' COMMENT '页面宽度。0表示自适应',
  `HEIGHT` int(11) DEFAULT '500' COMMENT '页面高度。0表示自适应',
  `ICON_CLASS` varchar(32) DEFAULT NULL COMMENT '图标样式类。为空表示不指定图标',
  `AUTO_FRESH_TIME` int(11) NOT NULL DEFAULT '0' COMMENT '自动刷新时间。单位：秒。默认0，表示不自动刷新',
  PRIMARY KEY (`PAGE_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=105007 DEFAULT CHARSET=utf8 COMMENT='页面配置表';

-- ----------------------------
-- Records of T_M_PAGES
-- ----------------------------
INSERT INTO `T_M_PAGES` VALUES ('101', '系统首页', '', null, null, '1', '1200', '500', null, '0');
INSERT INTO `T_M_PAGES` VALUES ('102', '舆情查询', '', null, null, '1', '1200', '500', null, '0');
INSERT INTO `T_M_PAGES` VALUES ('103', '舆情报表', null, null, null, '1', '1200', '500', null, '0');
INSERT INTO `T_M_PAGES` VALUES ('104', '舆情设置', null, null, null, '1', '1200', '500', null, '0');
INSERT INTO `T_M_PAGES` VALUES ('105', '系统管理', null, null, null, '1', '1200', '500', null, '0');
INSERT INTO `T_M_PAGES` VALUES ('102001', '仪表盘', null, null, null, '1', '1200', '500', null, '0');
INSERT INTO `T_M_PAGES` VALUES ('102002', '舆情查询', null, null, null, '1', '1200', '500', null, '0');
INSERT INTO `T_M_PAGES` VALUES ('102003', '预警信息查询', null, null, null, '1', '1200', '500', null, '0');
INSERT INTO `T_M_PAGES` VALUES ('102004', '月周报', null, null, null, '1', '1200', '500', null, '0');
INSERT INTO `T_M_PAGES` VALUES ('103001', '舆情信息条数统计', null, null, null, '1', '1200', '500', null, '0');
INSERT INTO `T_M_PAGES` VALUES ('103002', '倾向性条数统计', null, null, null, '1', '1200', '500', null, '0');
INSERT INTO `T_M_PAGES` VALUES ('103003', '趋势统计', null, null, null, '1', '1200', '500', null, '0');
INSERT INTO `T_M_PAGES` VALUES ('103004', '词汇频次图', null, null, null, '1', '1200', '500', null, '0');
INSERT INTO `T_M_PAGES` VALUES ('104001', '爬虫设置', null, null, null, '1', '1200', '500', null, '0');
INSERT INTO `T_M_PAGES` VALUES ('104002', '监控站点设置', null, null, null, '1', '1200', '500', null, '0');
INSERT INTO `T_M_PAGES` VALUES ('104003', '智能词条管理', null, null, null, '1', '1200', '500', null, '0');
INSERT INTO `T_M_PAGES` VALUES ('104004', '词汇管理', null, null, null, '1', '1200', '500', null, '0');
INSERT INTO `T_M_PAGES` VALUES ('104005', '预警设置', null, null, null, '1', '1200', '500', null, '0');
INSERT INTO `T_M_PAGES` VALUES ('105001', '菜单管理', 'redirect.do?jspName=menuMgr', null, null, '1', '1200', '500', null, '0');
INSERT INTO `T_M_PAGES` VALUES ('105002', '角色管理', 'redirect.do?jspName=roleMgr', null, null, '1', '1200', '500', null, '0');
INSERT INTO `T_M_PAGES` VALUES ('105003', '用户管理', 'redirect.do?jspName=userMgr', null, null, '1', '1200', '500', null, '0');
INSERT INTO `T_M_PAGES` VALUES ('105004', '授权管理', 'redirect.do?jspName=privilegeMgr', null, null, '1', '1200', '500', null, '0');
INSERT INTO `T_M_PAGES` VALUES ('105005', '用户信息', 'redirect.do?jspName=userInfo', null, null, '1', '1200', '500', null, '0');
INSERT INTO `T_M_PAGES` VALUES ('105006', '访问设置', 'redirect.do?jspName=accessMgr', null, null, '1', '1200', '500', null, '0');

-- ----------------------------
-- Table structure for `T_M_ROLES`
-- ----------------------------
DROP TABLE IF EXISTS `T_M_ROLES`;
CREATE TABLE `T_M_ROLES` (
  `ROLE_ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '角色编号',
  `ROLE_NAME` varchar(64) NOT NULL COMMENT '角色名称',
  `IS_VALID` char(1) NOT NULL DEFAULT '1' COMMENT '是否有效。1 有效；0 无效。默认1',
  PRIMARY KEY (`ROLE_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='角色信息表';

-- ----------------------------
-- Records of T_M_ROLES
-- ----------------------------
INSERT INTO `T_M_ROLES` VALUES ('1', '管理员组', '1');

-- ----------------------------
-- Table structure for `T_M_USERS`
-- ----------------------------
DROP TABLE IF EXISTS `T_M_USERS`;
CREATE TABLE `T_M_USERS` (
  `USER_ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户编号',
  `USER_NAME` varchar(64) NOT NULL COMMENT '用户名称',
  `USER_PASSWD` varchar(128) NOT NULL COMMENT '用户密码',
  `NICK_NAME` varchar(64) NOT NULL,
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `IS_VALID` char(1) NOT NULL DEFAULT '1' COMMENT '是否有效。1 有效；0 无效。默认1',
  `EMAIL` varchar(128) DEFAULT NULL,
  `PHONE_NUMBER` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`USER_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='用户信息表';

-- ----------------------------
-- Records of T_M_USERS
-- ----------------------------
INSERT INTO `T_M_USERS` VALUES ('1', 'admin', '202CB962AC59075B964B07152D234B70', '管理员', '2015-07-06 14:35:32', '2015-07-06 14:35:37', '1', null, null);

-- ----------------------------
-- Table structure for `T_M_WORD_TYPE`
-- ----------------------------
DROP TABLE IF EXISTS `T_M_WORD_TYPE`;
CREATE TABLE `T_M_WORD_TYPE` (
  `TYPE_ID` int(11) NOT NULL COMMENT '类别编号',
  `TYPE_NAME` varchar(32) NOT NULL COMMENT '类别名称',
  PRIMARY KEY (`TYPE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='词汇类别';

-- ----------------------------
-- Records of T_M_WORD_TYPE
-- ----------------------------
INSERT INTO `T_M_WORD_TYPE` VALUES ('1', '褒义');
INSERT INTO `T_M_WORD_TYPE` VALUES ('2', '贬义');
INSERT INTO `T_M_WORD_TYPE` VALUES ('3', '中性');

-- ----------------------------
-- Table structure for `T_PRI_ROLE_MENUS`
-- ----------------------------
DROP TABLE IF EXISTS `T_PRI_ROLE_MENUS`;
CREATE TABLE `T_PRI_ROLE_MENUS` (
  `ROLE_ID` int(11) NOT NULL COMMENT '角色编号',
  `MENU_ID` int(11) NOT NULL COMMENT '菜单编号',
  PRIMARY KEY (`ROLE_ID`,`MENU_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色菜单权限表';

-- ----------------------------
-- Records of T_PRI_ROLE_MENUS
-- ----------------------------
INSERT INTO `T_PRI_ROLE_MENUS` VALUES ('1', '101');
INSERT INTO `T_PRI_ROLE_MENUS` VALUES ('1', '102');
INSERT INTO `T_PRI_ROLE_MENUS` VALUES ('1', '103');
INSERT INTO `T_PRI_ROLE_MENUS` VALUES ('1', '104');
INSERT INTO `T_PRI_ROLE_MENUS` VALUES ('1', '105');
INSERT INTO `T_PRI_ROLE_MENUS` VALUES ('1', '106');
INSERT INTO `T_PRI_ROLE_MENUS` VALUES ('1', '102001');
INSERT INTO `T_PRI_ROLE_MENUS` VALUES ('1', '102002');
INSERT INTO `T_PRI_ROLE_MENUS` VALUES ('1', '102003');
INSERT INTO `T_PRI_ROLE_MENUS` VALUES ('1', '102004');
INSERT INTO `T_PRI_ROLE_MENUS` VALUES ('1', '103001');
INSERT INTO `T_PRI_ROLE_MENUS` VALUES ('1', '103002');
INSERT INTO `T_PRI_ROLE_MENUS` VALUES ('1', '105001');
INSERT INTO `T_PRI_ROLE_MENUS` VALUES ('1', '105002');
INSERT INTO `T_PRI_ROLE_MENUS` VALUES ('1', '105003');
INSERT INTO `T_PRI_ROLE_MENUS` VALUES ('1', '105004');
INSERT INTO `T_PRI_ROLE_MENUS` VALUES ('1', '105005');
INSERT INTO `T_PRI_ROLE_MENUS` VALUES ('1', '105006');

-- ----------------------------
-- Table structure for `T_PRI_ROLE_PAGES`
-- ----------------------------
DROP TABLE IF EXISTS `T_PRI_ROLE_PAGES`;
CREATE TABLE `T_PRI_ROLE_PAGES` (
  `ROLE_ID` int(11) NOT NULL DEFAULT '1' COMMENT '角色编号',
  `PAGE_ID` int(11) NOT NULL COMMENT '页面编号',
  PRIMARY KEY (`ROLE_ID`,`PAGE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色页面权限表';

-- ----------------------------
-- Records of T_PRI_ROLE_PAGES
-- ----------------------------
INSERT INTO `T_PRI_ROLE_PAGES` VALUES ('1', '101');
INSERT INTO `T_PRI_ROLE_PAGES` VALUES ('1', '102');
INSERT INTO `T_PRI_ROLE_PAGES` VALUES ('1', '103');
INSERT INTO `T_PRI_ROLE_PAGES` VALUES ('1', '104');
INSERT INTO `T_PRI_ROLE_PAGES` VALUES ('1', '105');
INSERT INTO `T_PRI_ROLE_PAGES` VALUES ('1', '102001');
INSERT INTO `T_PRI_ROLE_PAGES` VALUES ('1', '102002');
INSERT INTO `T_PRI_ROLE_PAGES` VALUES ('1', '102003');
INSERT INTO `T_PRI_ROLE_PAGES` VALUES ('1', '102004');
INSERT INTO `T_PRI_ROLE_PAGES` VALUES ('1', '103001');
INSERT INTO `T_PRI_ROLE_PAGES` VALUES ('1', '103002');
INSERT INTO `T_PRI_ROLE_PAGES` VALUES ('1', '103003');
INSERT INTO `T_PRI_ROLE_PAGES` VALUES ('1', '103004');
INSERT INTO `T_PRI_ROLE_PAGES` VALUES ('1', '104001');
INSERT INTO `T_PRI_ROLE_PAGES` VALUES ('1', '104002');
INSERT INTO `T_PRI_ROLE_PAGES` VALUES ('1', '104003');
INSERT INTO `T_PRI_ROLE_PAGES` VALUES ('1', '104004');
INSERT INTO `T_PRI_ROLE_PAGES` VALUES ('1', '104005');
INSERT INTO `T_PRI_ROLE_PAGES` VALUES ('1', '105001');
INSERT INTO `T_PRI_ROLE_PAGES` VALUES ('1', '105002');
INSERT INTO `T_PRI_ROLE_PAGES` VALUES ('1', '105003');
INSERT INTO `T_PRI_ROLE_PAGES` VALUES ('1', '105004');
INSERT INTO `T_PRI_ROLE_PAGES` VALUES ('1', '105005');
INSERT INTO `T_PRI_ROLE_PAGES` VALUES ('1', '105006');

-- ----------------------------
-- Table structure for `T_R_CRAWL_LOG`
-- ----------------------------
DROP TABLE IF EXISTS `T_R_CRAWL_LOG`;
CREATE TABLE `T_R_CRAWL_LOG` (
  `CRAWL_SEQ` bigint(20) NOT NULL COMMENT '爬取流水号',
  `SITE_ID` int(11) DEFAULT NULL COMMENT '站点编号',
  `START_TIME` datetime NOT NULL COMMENT '爬取开始时间',
  `END_TIME` datetime DEFAULT NULL COMMENT '爬取结束时间',
  `TOTAL_COST` bigint(20) NOT NULL DEFAULT '0' COMMENT '总耗时。单位秒',
  PRIMARY KEY (`CRAWL_SEQ`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='爬取日志';

-- ----------------------------
-- Records of T_R_CRAWL_LOG
-- ----------------------------

-- ----------------------------
-- Table structure for `T_R_PAGE`
-- ----------------------------
DROP TABLE IF EXISTS `T_R_PAGE`;
CREATE TABLE `T_R_PAGE` (
  `PAGE_SEQ` bigint(20) NOT NULL COMMENT '网页流水号',
  `SITE_ID` int(11) NOT NULL COMMENT '站点编号',
  `PAGE_URL` varchar(1024) NOT NULL COMMENT '网页地址',
  PRIMARY KEY (`PAGE_SEQ`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='爬取网页信息';

-- ----------------------------
-- Records of T_R_PAGE
-- ----------------------------

-- ----------------------------
-- Table structure for `T_R_PAGE_WORDS`
-- ----------------------------
DROP TABLE IF EXISTS `T_R_PAGE_WORDS`;
CREATE TABLE `T_R_PAGE_WORDS` (
  `PAGE_SEQ` bigint(20) NOT NULL COMMENT '网页流水号',
  `WORD_ID` int(11) NOT NULL COMMENT '词汇编号',
  `CNT` int(11) NOT NULL DEFAULT '1' COMMENT '出现次数',
  `IN_TIME` datetime NOT NULL COMMENT '入库时间',
  `IN_DATE` date DEFAULT NULL COMMENT '入库日期',
  PRIMARY KEY (`PAGE_SEQ`,`WORD_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='网页词汇信息表';

-- ----------------------------
-- Records of T_R_PAGE_WORDS
-- ----------------------------

-- ----------------------------
-- Table structure for `T_REL_PAGES`
-- ----------------------------
DROP TABLE IF EXISTS `T_REL_PAGES`;
CREATE TABLE `T_REL_PAGES` (
  `PAGE_ID` int(11) NOT NULL COMMENT '页面编号',
  `SUB_PAGE_ID` int(11) NOT NULL COMMENT '子页面编号',
  `ORDER_NUM` int(11) NOT NULL DEFAULT '1' COMMENT '显示顺序',
  `WIDTH` int(11) NOT NULL DEFAULT '600' COMMENT '宽度',
  `HEIGHT` int(11) NOT NULL DEFAULT '240' COMMENT '高度',
  `IS_VALID` char(1) NOT NULL DEFAULT '1' COMMENT '是否有效。可选值：0 无效；1 有效；默认1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='页面关系表';

-- ----------------------------
-- Records of T_REL_PAGES
-- ----------------------------

-- ----------------------------
-- Table structure for `T_REL_ROLE_USER`
-- ----------------------------
DROP TABLE IF EXISTS `T_REL_ROLE_USER`;
CREATE TABLE `T_REL_ROLE_USER` (
  `USER_ID` int(11) NOT NULL COMMENT '用户编号',
  `ROLE_ID` int(11) NOT NULL COMMENT '角色编号',
  PRIMARY KEY (`USER_ID`,`ROLE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户角色关联表';

-- ----------------------------
-- Records of T_REL_ROLE_USER
-- ----------------------------
INSERT INTO `T_REL_ROLE_USER` VALUES ('1', '1');

-- ----------------------------
-- Table structure for `T_U_CONFIG`
-- ----------------------------
DROP TABLE IF EXISTS `T_U_CONFIG`;
CREATE TABLE `T_U_CONFIG` (
  `USER_ID` int(11) NOT NULL COMMENT '用户编号',
  `CONFIG_NAME` varchar(64) NOT NULL COMMENT '配置名',
  `CONFIG_VALUE` varchar(256) DEFAULT NULL COMMENT '配置值',
  UNIQUE KEY `UNI_U_CONFIG` (`USER_ID`,`CONFIG_NAME`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户个性化配置表';

-- ----------------------------
-- Records of T_U_CONFIG
-- ----------------------------
INSERT INTO `T_U_CONFIG` VALUES ('-1', 'API_URL_PREFIX', 'http://192.168.6.121:8471');
INSERT INTO `T_U_CONFIG` VALUES ('-1', 'COLOR_NORMAL', 'blue');
INSERT INTO `T_U_CONFIG` VALUES ('-1', 'COLOR_WARN', 'red');
INSERT INTO `T_U_CONFIG` VALUES ('-1', 'DEFAULT_CLUSTER', 'AISC');

-- ----------------------------
-- Table structure for `T_U_KEY`
-- ----------------------------
DROP TABLE IF EXISTS `T_U_KEY`;
CREATE TABLE `T_U_KEY` (
  `USER_ID` int(11) NOT NULL COMMENT '用户编号',
  `KEY` varchar(128) NOT NULL COMMENT '密钥',
  `UPDATE_TIME` datetime NOT NULL COMMENT '更新时间',
  `INVALID_TIME` datetime NOT NULL COMMENT '失效时间',
  PRIMARY KEY (`USER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户密钥信息表';

-- ----------------------------
-- Records of T_U_KEY
-- ----------------------------
INSERT INTO `T_U_KEY` VALUES ('1', '65A2F024A244A7F7A3CB3DF43F4BE0C0', '2015-08-13 17:40:52', '2015-08-13 18:10:52');
