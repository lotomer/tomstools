create table T_WEB_METRIC_TEMPLATE
(
   TEMPLATE_ID          int not null auto_increment comment '模板编号',
   TEMPLATE_NAME        varchar(128) not null comment '模板名称',
   TEMPLATE_CONTENT     varchar(2048) comment '模板内容',
   TEMPLATE_SCRIPT      varchar(5000) comment '模板脚本',
   IS_VALID             char not null default '1' comment '是否有效。可选址：0 无效；1 有效。默认1',
   primary key (TEMPLATE_ID)
);

alter table T_WEB_METRIC_TEMPLATE comment '指标展示模板表';