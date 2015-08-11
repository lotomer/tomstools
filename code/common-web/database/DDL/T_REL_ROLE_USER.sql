create table T_REL_ROLE_USER
(
   USER_ID              int not null comment '用户编号',
   ROLE_ID              int not null comment '角色编号',
   primary key (USER_ID, ROLE_ID)
);

alter table T_REL_ROLE_USER comment '用户角色关联表';
