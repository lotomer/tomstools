<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>热点查询</title>
<link rel="stylesheet" type="text/css"
	href='css/easyui/themes/${theme}/easyui.css'>
<link rel="stylesheet" type="text/css" href="css/easyui/themes/icon.css">
<link rel="stylesheet" type="text/css" href="css/main.css">
</head>
<body class="easyui-layout">
	<div data-options="region:'north',split:true"
		style="height: 45px;padding:5px 30px;">
		<table>
			<tr>
				<!-- <td><label>开始时间：</label></td>
				<td><input id="st" class="easyui-datebox"
					data-options="showSeconds:false"></td>
				<td><label>结束时间：</label></td>
				<td><input id="et" class="easyui-datebox"
					data-options="showSeconds:false"></td> -->
                <td style="width:65px"><label>热点类型：</label></td>
                <td style="width:140px"><input id="flag" class="easyui-combobox" data-options="data:[{value:0,text:'行业热词'},{value:1,text:'网络热点'}]"></input></td>

				<td><a href="#" id="btnQuery" class="easyui-linkbutton"
					data-options="iconCls:'icon-search'" style="width: 120px">查询</a></td>
			</tr>
		</table>
	</div>

	<div id="divContent" data-options="region:'center',split:true">
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
	var theme = '${theme}', key = '${user.key}', flag = '${flag}';
	// 页面初始化
	$(function() {
		// 绑定事件
		$('#btnQuery').bind('click', query);
		$('#flag').combobox("select",flag);
		
		query();
	});
	
	
	/**
	 * 执行查询
	 */
	function query() {
		// 获取查询条件
		var containerId='divContent',
		flag = $('#flag').datetimebox('getValue');
		var params = {
				key : key,FLAG: flag
			};
		
		$('#' + containerId).html('');
		showLoading(containerId);
		var url = "crawl/query/hotword.do";
		//for(var name in params){
			//url = url + "&" + name + "=" + encodeURIComponent(params[name]);
		//}
		doQueryDetail(containerId, url,params);
	}
	
	function doQueryDetail(containerId,url,params){
		var divMetric = $('#' + containerId);
		$('#' + containerId).html('');
		//if (!datas) {
		//	return;
		//}
		$('#' + containerId).append('<div id="divMetric" style="width:99%;height:100%"></div>');
		var divMetric = $('#divMetric');
		var pageSize = 15;
		divMetric.datagrid({
			//title:'${title}',
			fitColumns : false,
			rownumbers : true,
			singleSelect : true,
			url: url,
			queryParams: params,
			//data : datas,
			//remoteSort : false,
			//idField : "DETAIL_SEQ",
			//sortName : "status",
			//sortOrder : "asc",
			pagination : true,//pageSize < datas.length,
			pageSize : pageSize,
			pageList : getPageList(pageSize),
			columns : [ [ {
				field : 'WORD',
				title : '内容',
				align : 'center',
				width: 500,
				halign : 'center',
				sortable : "true",
				formatter: function(value){
					return '<a href="#" onclick="javascript:window.top.createPageById(201006,\'&p=field:text,value:' + encodeURIComponent(value) + '\')" title="' +value+ '"  style="white-space: nowrap;display:block;overflow:hidden; text-overflow:ellipsis;">' + value+ '</a>';
				}
			}] ]
		});
	}
</script>
</html>
