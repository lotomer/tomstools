use nagios;
-- 查询主机组
DROP VIEW IF EXISTS V_HOST_GROUP;
CREATE VIEW V_HOST_GROUP AS 
SELECT hg.hostgroup_id group_id, name1 group_name,alias title 
FROM nagios_hostgroups hg 
LEFT JOIN nagios_objects o on (hg.hostgroup_object_id=o.object_id and o.objecttype_id=3 and o.is_active=1);

-- 查询服务组
DROP VIEW IF EXISTS V_SERVICE_GROUP;
CREATE VIEW V_SERVICE_GROUP AS 
SELECT sg.servicegroup_id group_id, name1 group_name, alias title FROM nagios_servicegroups sg
LEFT JOIN nagios_objects o ON (sg.servicegroup_object_id=o.object_id and o.objecttype_id=4 and o.is_active=1);

-- 查询主机组主机状态列表（has_been_checked：1=true, 0=false；current_state：0=UP, 1=DOWN, 2=UNREACHABLE）
DROP VIEW IF EXISTS V_HOST_STATUS_BY_GROUP;
CREATE VIEW V_HOST_STATUS_BY_GROUP AS
SELECT hgm.hostgroup_id group_id,h.host_object_id id,h.display_name name,h.alias title,h.address,
hs.status_update_time,hs.output,hs.perfdata,hs.current_state,hs.last_state_change,
hs.current_notification_number,hs.has_been_checked,o.name1 group_name,hg.alias group_title,
case UNIX_TIMESTAMP(hs.last_time_up)
WHEN 0 THEN ''
ELSE
hs.last_time_up
END last_time_up,
case UNIX_TIMESTAMP(hs.last_time_down)
WHEN 0 THEN ''
ELSE
hs.last_time_down
END last_time_down,
case UNIX_TIMESTAMP(hs.last_time_unreachable)
WHEN 0 THEN ''
ELSE
hs.last_time_unreachable
END last_time_unreachable,
CASE
WHEN has_been_checked=0 THEN 'PENDING'
WHEN current_state=0 THEN 'UP'
WHEN current_state=1 THEN 'DOWN'
WHEN current_state=2 THEN 'UNREACHABLE'
END status,
CASE
WHEN has_been_checked=0 THEN 2
WHEN current_state=0 THEN 1
WHEN current_state=1 THEN 4
WHEN current_state=2 THEN 3
END notify_level
FROM nagios_hostgroup_members hgm
LEFT JOIN nagios_hostgroups hg on (hgm.hostgroup_id=hg.hostgroup_id)
LEFT JOIN nagios_hosts h on (hgm.host_object_id=h.host_object_id)
LEFT JOIN nagios_hoststatus hs on (hgm.host_object_id=hs.host_object_id)
LEFT JOIN nagios_objects o on (hg.hostgroup_object_id=o.object_id)
WHERE h.host_object_id IS NOT NULL
ORDER BY notify_level DESC
;


-- 查询服务组服务状态列表（has_been_checked：1=true, 0=false；current_state：0=OK, 1=WARNING, 2=CRITICAL, 3=UNKNOWN）
DROP VIEW IF EXISTS V_SERVICE_STATUS_BY_GROUP;
CREATE VIEW V_SERVICE_STATUS_BY_GROUP AS
SELECT sgm.servicegroup_id group_id,s.service_object_id id,oo.name1 host_name,h.alias host_title,h.address,s.display_name title,ss.output,
ss.status_update_time,ss.obsess_over_service,ss.perfdata,ss.current_state,ss.last_state_change,h.host_object_id host_id,
ss.current_notification_number,ss.current_check_attempt,ss.has_been_checked,o.name1 group_name,sg.alias group_title,
case UNIX_TIMESTAMP(ss.last_time_ok)
WHEN 0 THEN ''
ELSE
ss.last_time_ok
END last_time_ok,
case UNIX_TIMESTAMP(ss.last_time_warning)
WHEN 0 THEN ''
ELSE
ss.last_time_warning
END last_time_warning,
case UNIX_TIMESTAMP(ss.last_time_critical)
WHEN 0 THEN ''
ELSE
ss.last_time_critical
END last_time_critical,
case UNIX_TIMESTAMP(ss.last_time_unknown)
WHEN 0 THEN ''
ELSE
ss.last_time_unknown
END last_time_unknown,
CASE
WHEN has_been_checked=0 THEN 'PENDING'
WHEN current_state=0 THEN 'OK'
WHEN current_state=1 THEN 'WARNING'
WHEN current_state=2 THEN 'CRITICAL'
WHEN current_state=3 THEN 'UNKNOWN'
END status,
CASE
WHEN has_been_checked=0 THEN 2
WHEN current_state=0 THEN 1
WHEN current_state=1 THEN 3
WHEN current_state=2 THEN 5
WHEN current_state=3 THEN 4
END notify_level
FROM nagios_servicegroup_members sgm
LEFT JOIN nagios_servicegroups sg ON (sgm.servicegroup_id=sg.servicegroup_id)
LEFT JOIN nagios_services s ON (sgm.service_object_id=s.service_object_id)
LEFT JOIN nagios_servicestatus ss ON (sgm.service_object_id=ss.service_object_id)
LEFT JOIN nagios_objects o on (sg.servicegroup_object_id=o.object_id)
LEFT JOIN nagios_objects oo on (sgm.service_object_id=oo.object_id)
LEFT JOIN nagios_hosts h ON (oo.name1=h.display_name)
ORDER BY notify_level DESC
;

-- 服务历史状态
DROP VIEW IF EXISTS V_HISTORY_SERVICE_STATUS;
CREATE VIEW V_HISTORY_SERVICE_STATUS AS
SELECT sc.servicecheck_id id,sc.service_object_id object_id,o.name1 host_name,h.alias host_title,o.name2 service_title,oc.name1,
sc.current_check_attempt,sc.max_check_attempts,CONCAT(sc.current_check_attempt,'/',sc.max_check_attempts) check_attempts,sc.state,sc.state_type,
sc.start_time,sc.end_time,sc.timeout,sc.return_code,sc.output,sc.perfdata,sg.servicegroup_id group_id,sg.alias group_title,
CASE state
WHEN 0 THEN 'OK'
WHEN 1 THEN 'WARNING'
WHEN 2 THEN 'CRITICAL'
WHEN 3 THEN 'UNKNOWN'
END status 
FROM nagios_servicechecks sc
LEFT JOIN nagios_objects o ON (sc.service_object_id=o.object_id)
LEFT JOIN nagios_hosts h ON (o.name1=h.display_name)
LEFT JOIN nagios_objects oc ON (sc.command_object_id=oc.object_id)
LEFT JOIN nagios_servicegroup_members sgm ON (sc.service_object_id=sgm.service_object_id)
LEFT JOIN nagios_servicegroups sg ON (sg.servicegroup_id=sgm.servicegroup_id)
;

-- 主机历史状态
DROP VIEW IF EXISTS V_HISTORY_HOST_STATUS;
CREATE VIEW V_HISTORY_HOST_STATUS AS
SELECT hc.hostcheck_id id,hc.host_object_id object_id,o.name1 host_name,h.alias host_title,oc.name1 command_name,c.command_line,
hc.command_object_id,hc.command_args,hc.current_check_attempt,hc.max_check_attempts,CONCAT(hc.current_check_attempt,'/',hc.max_check_attempts) check_attempts,
hc.state,hc.state_type,hc.start_time,hc.end_time,hc.timeout,hc.return_code,hc.output,hc.perfdata,hg.hostgroup_id group_id,hg.alias group_title,
CASE state
WHEN 0 THEN 'UP'
WHEN 1 THEN 'DOWN'
WHEN 2 THEN 'UNREACHABLE'
END status 
FROM nagios_hostchecks hc
LEFT JOIN nagios_objects o ON (hc.host_object_id=o.object_id)
LEFT JOIN nagios_hosts h ON (o.name1=h.display_name)
LEFT JOIN nagios_objects oc ON (hc.command_object_id=oc.object_id)
LEFT JOIN nagios_commands c ON (hc.command_object_id=c.object_id)
LEFT JOIN nagios_hostgroup_members hgm ON (hc.host_object_id=hgm.host_object_id)
LEFT JOIN nagios_hostgroups hg ON (hgm.hostgroup_id=hg.hostgroup_id)
;

-- 告警
DROP VIEW IF EXISTS V_NOTIFICATIONS;
CREATE VIEW V_NOTIFICATIONS AS
-- 主机
SELECT n.notification_id id,n.notification_type,'HOST' type,oo.name1 host_name,h.alias title,n.start_time,n.start_time_usec,n.end_time,n.end_time_usec,
CASE n.state
WHEN 0 THEN 'UP'
WHEN 1 THEN 'DOWN'
WHEN 2 THEN 'UNREACHABLE'
END status,
n.contacts_notified,n.output,ds.DICT_TITLE status_title
FROM nagios_notifications n
LEFT JOIN nagios_objects oo ON (oo.object_id=n.object_id )
LEFT JOIN nagios_hosts h ON (n.object_id=h.host_object_id)
LEFT JOIN nagios_contactnotifications cn ON (n.notification_id=cn.notification_id)
LEFT JOIN T_M_DICT ds ON (ds.TYPE='HOST_STATUS' and ds.DICT_NUM=n.state)
WHERE oo.objecttype_id=1
UNION ALL
-- 服务
SELECT n.notification_id id,n.notification_type,'SERVICE' type,oo.name1 host_name,oo.name2 title,n.start_time,n.start_time_usec,n.end_time,n.end_time_usec,
CASE n.state
WHEN 0 THEN 'OK'
WHEN 1 THEN 'WARNING'
WHEN 2 THEN 'CRITICAL'
WHEN 3 THEN 'UNKNOWN'
END status,n.contacts_notified,n.output,ds.DICT_TITLE status_title
FROM nagios_notifications n
LEFT JOIN nagios_objects oo ON (oo.object_id=n.object_id)
LEFT JOIN nagios_contactnotifications cn ON (n.notification_id=cn.notification_id)
LEFT JOIN T_M_DICT ds ON (ds.TYPE='SERVICE_STATUS' and ds.DICT_NUM=n.state)
WHERE oo.objecttype_id=2
;

-- 联系人组
DROP VIEW IF EXISTS V_CONTACT_GROUP;
CREATE VIEW V_CONTACT_GROUP AS
SELECT contactgroup_id id,o.name1 name,alias title 
FROM nagios_contactgroups cg 
LEFT JOIN nagios_objects o ON (cg.contactgroup_object_id=o.object_id)
;
-- 联系人
DROP VIEW IF EXISTS V_CONTACTS;
CREATE VIEW V_CONTACTS AS
SELECT c.contact_id id,c.alias title,cgm.contactgroup_id group_id
FROM nagios_contacts c 
LEFT JOIN nagios_contactgroup_members cgm ON (c.contact_object_id=cgm.contact_object_id)
;
-- 告警信息（联系人）
DROP VIEW IF EXISTS V_CONTACT_NOTIFICATIONS;
CREATE VIEW V_CONTACT_NOTIFICATIONS AS
SELECT c.contact_id,cn.start_time send_start_time,cn.start_time_usec send_start_time_usec,
cn.end_time send_end_time,cn.end_time_usec send_end_time_usec,cg.alias group_title,
cgm.contactgroup_id group_id,c.alias contact_title,c.email_address,o.name1 contact_name,n.*,dt.DICT_TITLE type_title,ds.DICT_TITLE notify_status_title
FROM nagios_contactnotifications cn
LEFT JOIN nagios_contacts c ON (cn.contact_object_id=c.contact_object_id)
LEFT JOIN nagios_objects o ON (cn.contact_object_id=o.object_id)
LEFT JOIN nagios_contactgroup_members cgm ON (cn.contact_object_id=cgm.contact_object_id)
LEFT JOIN nagios_contactgroups cg ON (cgm.contactgroup_id=cg.contactgroup_id)
LEFT JOIN V_NOTIFICATIONS n ON (cn.notification_id=n.id)
LEFT JOIN T_M_DICT dt ON (dt.TYPE='NOTIFICATION_TYPE' and dt.DICT_NAME=n.type)
LEFT JOIN T_M_DICT ds ON (ds.TYPE='NOTIFICATION_STATUS' and ds.DICT_NUM=n.contacts_notified)
ORDER BY send_end_time desc
;


