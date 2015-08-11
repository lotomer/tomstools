create table T_REL_PAGES
(
   PAGE_ID              int not null comment '页面编号',
   SUB_PAGE_ID          int not null comment '子页面编号',
   ORDER_NUM            int not null default 1 comment '显示顺序',
   WIDTH                int not null default 600 comment '宽度',
   HEIGHT               int not null default 240 comment '高度',
   IS_VALID             char not null default '1' comment '是否有效。可选值：0 无效；1 有效；默认1'
);

alter table T_REL_PAGES comment '页面关系表';