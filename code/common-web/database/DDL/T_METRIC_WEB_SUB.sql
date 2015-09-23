create table T_METRIC_WEB_SUB
(
   SUB_WEB_METRIC_ID    int not null auto_increment comment 'WEB指标子编号',
   SUB_WEB_METRIC_CODE  varchar(128) not null comment 'WEB指标子编码',
   URL                  varchar(512) not null comment 'WEB网址URL',
   URL_BAK              varchar(512) comment '备用WEB网址URL',
   CONTENT_TYPE         varchar(32) not null default 'json' comment '内容类型。可选值：json、html或xml',
   PAGE_ENCODING        varchar(32) not null default 'UTF-8' comment '页面内容字符集',
   HEADERS              varchar(512) comment 'HTTP请求头信息。以json对象字符串格式保存，支持"user."、"config."和"request."开头的变量如{user:"${user.userId}",header2:"${config.config1}"}',
   IS_VALID             char not null default '1' comment '是否有效。可选址：0 无效；1 有效。默认1',
   primary key (SUB_WEB_METRIC_ID)
);

alter table T_METRIC_WEB_SUB comment 'WEB指标配置子表';

/*==============================================================*/
/* Index: U_METRIC_ID_SUBCODE                                   */
/*==============================================================*/
create unique index U_METRIC_ID_SUBCODE on T_METRIC_WEB_SUB
(
   SUB_WEB_METRIC_CODE
);

