create table T_M_PAGES
(
   PAGE_ID              int not null auto_increment comment '页面编号',
   PAGE_NAME            varchar(64) not null comment '页面名称',
   CONTENT_URL          varchar(512) comment '内容对应的URL',
   PARAMS               varchar(512) comment '页面参数',
   IN_TIME              datetime comment '生成时间',
   IS_VALID             char(1) not null default '1' comment '是否有效。1 有效；0 无效。默认1',
   WIDTH                int default 1200 comment '页面宽度。0表示自适应',
   HEIGHT               int default 500 comment '页面高度。0表示自适应',
   ICON_CLASS           varchar(32) comment '图标样式类。为空表示不指定图标',
   AUTO_FRESH_TIME      int not null default 0 comment '自动刷新时间。单位：秒。默认0，表示不自动刷新',
   UPDATE_TIME          datetime comment '更新时间',
   primary key (PAGE_ID)
);

alter table T_M_PAGES comment '页面配置表';