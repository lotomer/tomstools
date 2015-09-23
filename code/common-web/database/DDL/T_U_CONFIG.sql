create table T_U_CONFIG
(
   USER_ID              int not null comment '用户编号',
   CONFIG_NAME          varchar(64) not null comment '配置名',
   CONFIG_VALUE         varchar(512) comment '配置值',
   CONFIG_TITLE         varchar(64) comment '配置说明'
);

alter table T_U_CONFIG comment '用户个性化配置表';

create unique index UNI_U_CONFIG on T_U_CONFIG
(
   USER_ID,
   CONFIG_NAME
);