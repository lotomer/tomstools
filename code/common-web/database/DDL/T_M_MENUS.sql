create table T_M_MENUS
(
   MENU_ID              int not null auto_increment comment '菜单编号',
   PAGE_ID              int comment '页面编号',
   MENU_NAME            varchar(64) not null comment '菜单名称',
   PARENT_ID            int not null default -1 comment '父编号。默认为-1，表示一级模块。',
   IN_TIME              datetime comment '生成时间',
   ICON_CLASS           varchar(32) comment '图标样式类。为空表示不指定图标',
   ORDER_NUM            int not null default 0 comment '显示顺序',
   IS_SHOW              char not null default '0' comment '是否显示。可选值：0 不显示；1 显示',
   IS_VALID             char(1) not null default '1' comment '是否有效。1 有效；0 无效。默认1',
   UPDATE_TIME          datetime comment '更新时间',
   primary key (MENU_ID)
);

alter table T_M_MENUS comment '菜单配置表';