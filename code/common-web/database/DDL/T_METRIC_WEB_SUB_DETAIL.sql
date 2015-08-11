create table T_METRIC_WEB_SUB_DETAIL
(
   SUB_WEB_METRIC_ID    int not null comment 'WEB指标子编号',
   METRIC_TITLE         varchar(128) not null comment '指标说明',
   METRIC_SELECTOR      varchar(256) not null comment '指标值选择器',
   VALUE_TYPE           varchar(128) comment '取值属性。空表示取正文；非空则取对应的属性值',
   ORDER_NUM            int not null default 1 comment '显示顺序',
   IS_VALID             char not null default '1' comment '是否有效。可选值：0 无效；1有效。默认1'
);

alter table T_METRIC_WEB_SUB_DETAIL comment 'WEB指标子配置明细表';
