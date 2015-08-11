create table T_M_ROLES
(
   ROLE_ID              int not null auto_increment comment '角色编号',
   ROLE_NAME            varchar(64) not null comment '角色名称',
   IS_VALID             char not null default '1' comment '是否有效。1 有效；0 无效。默认1',
   primary key (ROLE_ID)
);

alter table T_M_ROLES comment '角色信息表';