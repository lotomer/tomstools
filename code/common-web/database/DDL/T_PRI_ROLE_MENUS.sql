create table T_PRI_ROLE_MENUS
(
   ROLE_ID              int not null comment '角色编号',
   MENU_ID              int not null comment '菜单编号',
   primary key (ROLE_ID, MENU_ID)
);

alter table T_PRI_ROLE_MENUS comment '角色菜单权限表';
