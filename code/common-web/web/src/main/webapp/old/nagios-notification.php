<?php
error_reporting(E_ERROR);
session_start();
$current_dir  = dirname(__FILE__);
$doc_root = $current_dir . '/';
require_once $doc_root . 'action/validate.php';
require_once $doc_root . 'dao/db.php';
?>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>告警信息查询</title>
    <link rel="stylesheet" type="text/css" href="css/easyui/themes/<?php echo isset( $_GET["theme"] )? $_GET["theme"] : "default"; ?>/easyui.css">
    <link rel="stylesheet" type="text/css" href="css/easyui/themes/icon.css">
    <link rel="stylesheet" type="text/css" href="css/main.css">
</head>
<body class="easyui-layout">
    <div data-options="region:'center',split:true">
        <div>
            <table>
                <tr>
                    <td style="width:65px"><label>联系人组：</label></td>
                    <td style="width:140px"><input id="notifyContactGroup" class="easyui-combobox"></input></td>
                    <td style="width:65px"><label>联系人：</label></td>
                    <td style="width:140px"><input id="notifyContact" class="easyui-combobox"></input></td>
                    <td style="width:65px"><label>类别：</label></td>
                    <td style="width:140px"><input id="notifyType" class="easyui-combobox"></input></td>
                </tr>
                <tr>
                    <td><label>开始时间：</label></td>
                    <td><input id="notifyStartTime" class="easyui-datetimebox"></td>
                    <td><label>结束时间：</label></td>
                    <td><input id="notifyEndTime" class="easyui-datetimebox"></td>
                    <td></td>
                    <td><a href="#" id="btnSearchNotification" class="easyui-linkbutton" data-options="iconCls:'icon-search'" style="width:80px">查询</a></td>
                </tr>
            </table>
        </div>
        <!-- <div id="divServiceStatusContent" class="content-result">
        </div> -->
        <div style="clear:both;width:100%;">
            <table id="contactNotificationDetails"></table>
        </div>
    </div>    
    <script type="text/javascript" src="js/jquery.min.js"></script>
    <script type="text/javascript" src="js/easyui/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="js/easyui/locale/easyui-lang-zh_CN.js"></script>
    <!--[if lte IE 8]><script language="javascript" type="text/javascript" src="/js/flot/excanvas.min.js"></script><![endif]-->
    <script type="text/javascript" src="js/flot/jquery.flot.min.js"></script>
    <script type="text/javascript" src="js/flot/jquery.flot.pie.min.js"></script>
    <script type="text/javascript">
        /**
    	 *  日期格式转换。将yyyy-MM-dd转换为MM/dd/yyyy 
    	 */
    	function dateStringFormatter(s){
    		if (!s) return '';
    		var ss = s.split('-');
    		if (3 == ss.length){
    		    var ds = ss[2].split(' '),
    		        ret = ss[1] + '/' + ds[0] + '/' + ss[0];
    		    if (1 < ds.length) {
                    ret += ' ' + ds[1];
                }
                
                return ret;
    		}else {
                return s;
            }
    	};
        $(function(){
            // 加载联系人分组信息
            $('#notifyContactGroup').combobox({
                valueField: 'id',
                textField: 'title',
                panelHeight: 'auto',
                editable: false,
                data: getContactGroups(),
                onChange: function(newValue,oldValue) {
                    loadContacts(newValue);
                }
            }).combobox("select","*");
            // 加载联系人
            loadContacts()
            // 加载类型信息
            $('#notifyType').combobox({
                valueField: 'id',
                textField: 'title',
                panelHeight: 'auto',
                editable: false,
                data: getNotifyType()
            }).combobox("select","*");
            
            // 绑定事件
            $('#btnSearchNotification').bind('click', searchContactNotifications);
            
            // 执行首次查询
            searchContactNotifications();
        });
       /**
         * 获取分组下的所有联系人。
         * @param groupId 分组编号。不能为空
         */
        function loadContacts (groupId) {
            var contacts = getContactDatas(groupId);
            $('#notifyContact').combobox({
                valueField: 'id',
                textField: 'title',
                panelHeight: 'auto',
                editable: false,
                data: contacts
            }).combobox("select","*");
    }
        function getContactDatas (groupId) {
            return getComboboxData(eval('<?php echo addslashes(DB::getInstance()->select4json("select id,title,group_id from V_CONTACTS"));?>'),
                function(data){
                    if (groupId && groupId != data.group_id) {
                        return true;
                    }else {
                        return false;
                    }
                });
        }
        function getContactGroups () {
            return getComboboxData(eval('<?php echo addslashes(DB::getInstance()->select4json("select id,title from V_CONTACT_GROUP"));?>'));
        }
        function getNotifyType () {
            return getComboboxData(eval('<?php echo addslashes(DB::getInstance()->select4json("select DICT_NAME id,DICT_TITLE title from T_M_DICT where TYPE='NOTIFICATION_TYPE'"));?>'));
        }
        function getComboboxData (datas,filter) {
            var result = [],o = new Object();
            o.id = '*';
            o.title = '-- 请选择 --';
            result.push(o);
            if (datas) {
                for (var i = 0,len = datas.length; i < len; i++) {
                    if(!filter || !filter(datas[i])) result.push(datas[i]);
                }
            }

            return result;
        }
        /**
         * 清理内容区域
         */
        function clearContent () {
            $('#contactNotificationDetails').html('');
        }
        function searchContactNotifications () {// 获取查询条件
            var notifyContactGroup = $('#notifyContactGroup').combobox('getValue'), // 联系人组
                notifyContact = $('#notifyContact').combobox('getValue'),           // 联系人
                notifyType = $('#notifyType').combobox('getValue'),                 // 通知种类
                startTime = $('#notifyStartTime').datetimebox('getValue'),          // 开始时间
                endTime = $('#notifyEndTime').datetimebox('getValue');              // 开始时间
            clearContent();
            
            // 趋势图
            
            // 获取明细
            $('#contactNotificationDetails').datagrid({
                width: 1185,
                height: 410,
                method: 'GET',
                pageSize: 14,
                pageNumber:1,
                pageList : [14,30,50,100],
                fitColumns: true,
                autoRowHeight: true,
                pagination: true,
                striped: true,
                rownumbers: true,
                idField: 'id',
                url: 'action.php',
                queryParams: {
            		q: 'CONTACT_NOTIFICATION',
            		groupId: '*' == notifyContactGroup ? '' : notifyContactGroup,
            		contactId: '*' == notifyContact ? '' : notifyContact,
            		type: '*' == notifyType ? '' : notifyType,
            		startTime: startTime,
            		endTime: endTime
            	},
                singleSelect: true,
                sortName: 'id',
                sortOrder: 'desc',
                scrollbarSize : 10,
                columns:[[
                    {field:'title',title:'名称',halign:'center'},
                    {field:'type_title',title:'类别',halign:'center'},
                    {field:'status_title',title:'状态',halign:'center',styler:statusStyler},
                    {field:'start_time',title:'开始时间',halign:'center'},
                    {field:'end_time',title:'结束时间',halign:'center'},
                    {field:'notify_status_title',title:'通知状态',halign:'center',align:'center'},
                    {field:'contact_title',title:'联系人',halign:'center',align:'center'},
                    {field:'email_address',title:'E-mail',halign:'center',align:'center'},
                    {field:'group_title',title:'联系人组',halign:'center'},
                    {field:'output',title:'信息',halign:'center'}
                ]]
                //,rowStyler: serviceRowStyler
            });
        }

        function statusStyler(value1,row,index){
            var value = row.status;
            if (value == 'PENDING'){
            	return 'background-color:#acacac;color:#000;'; 
            }else if (value == 'OK' || value == 'UP') {
                return 'background-color:#88d066;color:#000;';
            }else if (value == 'WARNING') {
                return 'background-color:#ffff00;color:#000;';
            }else if (value == 'CRITICAL' || value == 'DOWN') {
                return 'background-color:#f88888;color:#000;';
            }else if (value == 'UNKNOWN' || value == 'UNREACHABLE') {
                return 'background-color:#ffbb55;color:#000;';
            }
        }
    </script>
</body>
</html>