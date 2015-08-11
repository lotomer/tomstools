create table T_REL_WEB_METRIC_SUB
(
   WEB_METRIC_ID        int not null comment 'WEB指标编号',
   SUB_WEB_METRIC_ID    int not null comment 'WEB指标子编号',
   primary key (WEB_METRIC_ID, SUB_WEB_METRIC_ID)
);

alter table T_REL_WEB_METRIC_SUB comment 'WEB指标配置与子配置关系表';
