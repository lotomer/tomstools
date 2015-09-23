create table T_U_KEY
(
   USER_ID              int not null comment '用户编号',
   `KEY`                varchar(128) not null comment '密钥',
   UPDATE_TIME          datetime not null comment '更新时间',
   INVALID_TIME         datetime not null comment '失效时间',
   primary key (`KEY`)
);

alter table T_U_KEY comment '用户密钥信息表';