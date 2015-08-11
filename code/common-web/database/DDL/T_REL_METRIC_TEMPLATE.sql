create table T_REL_METRIC_TEMPLATE
(
   WEB_METRIC_ID        int not null comment 'WEB指标编号',
   TEMPLATE_ID          int not null comment '模板编号',
   IS_VALID             char not null default '1' comment '是否有效。可选址：0 无效；1 有效。默认1'
);

alter table T_REL_METRIC_TEMPLATE comment 'WEB指标与模板关系表';
