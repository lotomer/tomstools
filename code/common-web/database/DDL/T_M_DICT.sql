create table T_M_DICT
(
   TYPE                 varchar(64) not null comment '类别',
   DICT_NUM             varchar(64) not null comment '字段编号',
   DICT_NAME            varchar(64) not null comment '字段名称',
   DICT_TITLE           varchar(64) not null comment '字段描述'
);

alter table T_M_DICT comment '字典表';

/*==============================================================*/
/* Index: UNI_TYPE_NUM                                          */
/*==============================================================*/
create unique index UNI_TYPE_NUM on T_M_DICT
(
   TYPE,
   DICT_NUM
);

/*==============================================================*/
/* Index: UNI_TYPE_NAME                                         */
/*==============================================================*/
create unique index UNI_TYPE_NAME on T_M_DICT
(
   TYPE,
   DICT_NAME
);