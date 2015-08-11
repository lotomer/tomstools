create table T_METRIC_WEB
(
   WEB_METRIC_ID        int not null auto_increment comment 'WEB指标编号',
   WEB_METRIC_CODE      varchar(64) not null comment '指标配置编码',
   WEB_METRIC_TITLE     varchar(128) not null comment '指标配置名称',
   IS_VALID             char not null default '1' comment '是否有效。可选址：0 无效；1 有效。默认1',
   primary key (WEB_METRIC_ID)
);

alter table T_METRIC_WEB comment 'WEB指标配置表';

/*==============================================================*/
/* Index: U_WEB_METRIC_CODE                                     */
/*==============================================================*/
create unique index U_WEB_METRIC_CODE on T_METRIC_WEB
(
   WEB_METRIC_CODE
);
