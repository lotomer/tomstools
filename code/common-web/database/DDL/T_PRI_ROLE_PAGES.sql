create table T_PRI_ROLE_PAGES
(
   ROLE_ID              int not null default 1 comment '角色编号',
   PAGE_ID              int not null comment '页面编号',
   primary key (ROLE_ID, PAGE_ID)
);

alter table T_PRI_ROLE_PAGES comment '角色页面权限表';
