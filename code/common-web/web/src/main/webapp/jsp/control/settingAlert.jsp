<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>预警设置</title>
<link rel="stylesheet" type="text/css"
	href='css/easyui/themes/${theme}/easyui.css'>
<link rel="stylesheet" type="text/css" href="css/easyui/themes/icon.css">
<link rel="stylesheet" type="text/css" href="css/main.css">
<style type="text/css">
#fm, #fmDetail {
	margin: 0;
	padding: 10px 10px;
}

.ftitle {
	font-size: 14px;
	font-weight: bold;
	padding: 5px 0;
	margin-bottom: 10px;
	border-bottom: 1px solid #ccc;
}

.fitem {
	margin-bottom: 5px;
}

.fitem label {
	display: inline-block;
	width: 80px;
}

.fitem input {
	width: 180px;
}
</style>
</head>
<body class="easyui-layout">
	<div id="divStatWordsContent" data-options="region:'west',split:true"
		style="width: 100%;"></div>
	<div id="divContentDetail" data-options="region:'center',split:true">
	</div>
	<div id="dlg" class="easyui-dialog" data-options="modal:true"
		style="width: 340px; height: 250px; padding: 10px 10px" closed="true"
		buttons="#dlg-buttons">
		<!-- <div class="ftitle">智能词条</div> -->
		<form id="fm" method="post" novalidate>
			<input type="hidden" name="ALERT_ID" id="ALERT_ID" /> <input
				type="hidden" name="ALERT_TYPE" id="ALERT_TYPE" /><input
				type="hidden" name="METRICS" id="METRICS" /><input
				type="hidden" name="NOTIFIERS" id="NOTIFIERS" />
			<div class="fitem">
				<label>通知方式：</label> <input id="selAlertType" class="easyui-combobox"  data-options='editable: false,data:[{value:0,text:"站内通知"},{value:1,text:"邮件通知"},{value:2,text:"短信通知"}]'
					required="required"></input>
			</div>
			<div class="fitem">
				<label>预警名称：</label> <input name="ALERT_NAME" class="easyui-textbox"
					required="required">
			</div>
			<div class="fitem">
				<label>预警值：</label> <input name="ALERT_VALUE" class="easyui-textbox "
					required="required">
			</div>
			<div class="fitem">
				<label>监控词条：</label> <input id="selMETRICS" class="easyui-combobox" data-options="multiple:true"
					required="required">
			</div>
			<div class="fitem">
				<label>通知对象：</label> <input id="selNOTIFIERS" class="easyui-combobox" data-options="multiple:true"
					required="required">
			</div>
		</form>
	</div>
	<div id="dlg-buttons">
		<a href="javascript:void(0)" class="easyui-linkbutton c6"
			iconCls="icon-ok" onclick="save()" style="width: 90px">保存</a> <a
			href="javascript:void(0)" class="easyui-linkbutton"
			iconCls="icon-cancel" onclick="javascript:$('#dlg').dialog('close')"
			style="width: 90px">取消</a>
	</div>
	<div id="tb" style="height: auto">
		<a href="javascript:void(0)" class="easyui-linkbutton"
			data-options="iconCls:'icon-add',plain:true" onclick="add()">新增</a> <a
			href="javascript:void(0)" class="easyui-linkbutton"
			data-options="iconCls:'icon-remove',plain:true" onclick="removeit()">删除</a>
		<a href="javascript:void(0)" class="easyui-linkbutton"
			data-options="iconCls:'icon-edit',plain:true" onclick="edit()">修改</a>
	</div>
</body>
<script type="text/javascript" src="js/jquery.min.js"></script>
<script type="text/javascript" src="js/easyui/jquery.easyui.min.js"></script>
<script type="text/javascript"
	src="js/easyui/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript">
	// 面板与内容之间的差值
	var theme = '${theme}', key = '${user.key}';
	// 页面初始化
	$(function() {
		// 绑定事件
		query();
		
		initCombobox("selMETRICS", "setting/words/select.do?key=" + key, function(record) {
			if (record != undefined && record.id != undefined)
				$('#METRICS').val(record.id);
		}, true,"TYPE_ID","TYPE_NAME");
		initCombobox("selNOTIFIERS", "sys/user/list.do?key=" + key, function(record) {
			if (record != undefined && record.id != undefined)
				$('#NOTIFIERS').val(record.id);
		}, true,"USER_ID","NICK_NAME");
		
		//selectCombobox();
	});
	function selectCombobox(data) {
		if (!data) {
			
		} else {
			$('#selAlertType').combobox("select", data.ALERT_TYPE);
			$('#selMETRICS').combobox("setValues", data.METRICS.split("$$$")[0]);
			$('#selNOTIFIERS').combobox("setValues", data.NOTIFIERS.split("$$$")[0]);
		}
	}
	var url, siteData;
	function add() {
		$('#dlg').dialog('open').dialog('setTitle', '新增预警设置');
		$('#fm').form('clear');
		$('#selAlertType').combobox("select","0");
		url = 'setting/alert/add.do?key=' + key;
	}
	function edit() {
		var row = $('#divMetric').datagrid('getSelected');
		if (row) {
			$('#dlg').dialog('open').dialog('setTitle', '修改预警设置');
			$('#fm').form('clear').form('load', row);
			url = 'setting/alert/save.do?key=' + key + '&id=' + row.ALERT_ID;
			selectCombobox(row);
		}
	}
	function save() {
		var metrics = $('#selMETRICS').combobox("getValues").join(",") + '$$$' + $('#selMETRICS').combobox("getText"), 
			notifiers = $('#selNOTIFIERS').combobox("getValues").join(",") + '$$$' + $('#selNOTIFIERS').combobox("getText"), 
			alertType = $('#selAlertType').combobox("getValue");
		
		$('#ALERT_TYPE').val(alertType);
		$('#METRICS').val(metrics);
		$('#NOTIFIERS').val(notifiers);
		$('#fm').form('submit', {
			url : url,
			onSubmit : function() {
				return $(this).form('validate');
			},
			success : function(result) {
				if (result) {
					showErrorMessage('操作失败',result);
				} else {
					$('#dlg').dialog('close'); // close the dialog
					$('#divMetric').datagrid("reload"); // reload the user data
				}
			}
		});
	}
	function removeit() {
		var row = $('#divMetric').datagrid('getSelected');
		if (row) {
			$.messager.confirm('删除确认', '确定要删除吗？', function(r) {
				if (r) {
					$.post("setting/alert/delete.do", {
						id : row.ALERT_ID,
						key : key
					}, function(result) {
						if (!result) {
							$('#divMetric').datagrid('reload'); // reload the user data 
							$('#divMetric').datagrid('unselectAll');
						} else {
							showErrorMessage('操作失败',result);
						}
					}, 'html');
				}
			});
		}
	}

	/**
	 * 执行查询
	 */
	function query() {
		$('#divStatWordsContent').html('');
		showLoading("divStatWordsContent");
		$('#divStatWordsContent').html('');
		$('#divStatWordsContent').append(
				'<div id="divMetric" style="width:100%;height:100%"></div>');
		var divMetric = $('#divMetric'), pageSize = 15;
		divMetric.datagrid({
			toolbar : '#tb',
			title : '预警设置列表',
			url : "setting/alert/select.do",
			fitColumns : true,
			rownumbers : true,
			singleSelect : true,
			remoteSort : false,
			idField : "ALERT_ID",
			//sortName : "status",
			//sortOrder : "asc",
			pagination : true,
			pageSize : pageSize,
			pageList : getPageList(pageSize),
			columns : [ [ {
				field : 'ALERT_NAME',
				title : '预警名称',
				align : 'center',
				halign : 'center'
			}, {
				field : 'ALERT_TYPE',
				title : '通知方式',
				align : 'center',
				halign : 'center',
				formatter: function(value){
					if ("1" == value){
						return "邮件通知";
					}else if ("2" == value){
						return "短信通知";
					} else{
						return "站内通知";
					}
				}
			}, {
				field : 'ALERT_VALUE',
				title : '预警值',
				align : 'center',
				halign : 'center'
			}, {
				field : 'METRICS',
				title : '监控词条列表',
				align : 'center',
				halign : 'center',
				formatter: function(value){
					return value.split('$$$')[1];
				}
			}, {
				field : 'NOTIFIERS',
				title : '通知对象列表',
				align : 'center',
				halign : 'center',
				formatter: function(value){
					return value.split('$$$')[1];
				}
			}] ]
		}).datagrid('clientPaging');
	}
</script>
</html>
