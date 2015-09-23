create table T_M_USERS
(
   USER_ID              int not null auto_increment comment '用户编号',
   USER_NAME            varchar(64) not null comment '用户名称',
   USER_PASSWD          varchar(128) not null comment '用户密码',
   NICK_NAME            varchar(64) not null,
   CREATE_TIME          datetime comment '创建时间',
   UPDATE_TIME          datetime comment '更新时间',
   IS_VALID             char(1) not null default '1' comment '是否有效。1 有效；0 无效。默认1',
   EMAIL                varchar(128),
   PHONE_NUMBER         varchar(32),
  `CLIENT_IP` varchar(128) DEFAULT NULL COMMENT '客户端限制',
   primary key (USER_ID)
);

alter table T_M_USERS comment '用户信息表';