<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>预警查询</title>
<link rel="stylesheet" type="text/css"
	href='css/easyui/themes/${theme}/easyui.css'>
<link rel="stylesheet" type="text/css" href="css/easyui/themes/icon.css">
<link rel="stylesheet" type="text/css" href="css/main.css">
</head>
<body class="easyui-layout">
	<div data-options="region:'north',split:true"
		style="height: 80px;padding:5px 30px;">
		<table>
			<tr>
				<td ><label>通知方式：</label></td>
                <td><input id="selAlertType" data-options='editable: false,data:[{value:"*",text:"--请选择--"},{value:0,text:"站内通知"},{value:1,text:"邮件通知"},{value:2,text:"短信通知"}]' class="easyui-combobox"></input></td>
                <td ><label>通知状态：</label></td>
                <td><input id="selNotifyStatus" data-options='editable: false,data:[{value:"*",text:"--请选择--"},{value:0,text:"未通知"},{value:1,text:"已通知"},{value:2,text:"通知失败"}]' class="easyui-combobox"></input></td>
			</tr>
			<tr>
				<td><label>开始时间：</label></td>
				<td><input id="st" class="easyui-datebox"
					data-options="showSeconds:false"></td>
				<td><label>结束时间：</label></td>
				<td><input id="et" class="easyui-datebox"
					data-options="showSeconds:false"></td>
                <td></td>
				<td><a href="#" id="btnQuery" class="easyui-linkbutton"
					data-options="iconCls:'icon-search'" style="width: 80px">查询</a></td>
			</tr>
		</table>
	</div>

	<div id="divStatWordsContent" data-options="region:'center',split:true">
	</div>
</body>
<!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
	  <script src="js/echarts/html5shiv.min.js"></script>
	  <script src="js/echarts/respond.min.js"></script>
	<![endif]-->
<script type="text/javascript" src="js/echarts/echarts.js"></script>
<script type="text/javascript" src="js/jquery.min.js"></script>
<script type="text/javascript" src="js/easyui/jquery.easyui.min.js"></script>
<script type="text/javascript"
	src="js/easyui/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/myEcharts.js"></script>
<script type="text/javascript">
	// 面板与内容之间的差值
	var theme = '${theme}', key = '${user.key}';
	// 页面初始化
	$(function() {
		// 绑定事件
		$('#btnQuery').bind('click', query);
		$('#selAlertType').combobox("select","*");
		$('#selNotifyStatus').combobox("select","*");
		query();
	});
	
	
	/**
	 * 执行查询
	 */
	function query() {
		// 获取查询条件
		var startTime = $('#st').datetimebox('getValue'), // 开始时间
		endTime = $('#et').datetimebox('getValue') // 结束时间
		,notifyStatus = $('#selNotifyStatus').combobox("getValue")
		,alertType = $('#selAlertType').combobox("getValue");
		$('#divStatWordsContent').html('');
		showLoading("divStatWordsContent");
		var params = {
				key : key,
				startTime : startTime,
				endTime : endTime
			};
		if (alertType != undefined && alertType != "*"){
			params["ALERT_TYPE"] = alertType;
		}
		if (notifyStatus != undefined && notifyStatus != "*"){
			params["NOTIFY_STATUS"] = notifyStatus;
		}
		doQuery("divStatWordsContent", "crawl/query/alertQuery.do",params);
	}
	function doQuery(containerId,url,params) {
		var divMetric = $('#' + containerId);
		$('#' + containerId).html('');
				$('#' + containerId).append('<div id="divMetric" style="width:100%;height:100%"></div>');
		var divMetric = $('#divMetric');
		var pageSize = 13;
		divMetric.datagrid({
			//title:'${title}',
			fitColumns : true,
			rownumbers : true,
			singleSelect : true,
			//remoteSort : false,
			url : url,
			queryParams: params,
			//data : datas,
			idField : "ID",
			sortName : "ALERT_TIME",
			sortOrder : "desc",
			pagination : true,//pageSize < datas.length,
			pageSize : pageSize,
			pageList : getPageList(pageSize),
			columns : [ [ {
				field : 'ALERT_NAME',
				title : '预警名',
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
			} , {
				field : 'CURRENT_VALUE',
				title : '当前值',
				align : 'center',
				halign : 'center'
			} , {
				field : 'ALERT_TIME',
				title : '预警时间',
				align : 'center',
				halign : 'center'
			}, {
				field : 'NOTIFY_STATUS',
				title : '通知状态',
				align : 'center',
				halign : 'center',
				formatter: function(value){
					if ("1" == value){
						return "已通知";
					}else if ("2" == value){
						return "通知失败";
					} else{
						return "未通知";
					}
				}
			}, {
				field : 'NOTIFY_TIME',
				title : '通知时间',
				align : 'center',
				halign : 'center'
			} ] ]
		});//.datagrid('clientPaging');
	}
</script>
</html>
