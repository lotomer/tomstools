<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>最新舆情查询</title>
<link rel="stylesheet" type="text/css"
	href='css/easyui/themes/${theme}/easyui.css'>
<link rel="stylesheet" type="text/css" href="css/easyui/themes/icon.css">
<link rel="stylesheet" type="text/css" href="css/main.css">
</head>
<body class="easyui-layout">
	<div data-options="region:'north',split:true"
		style="height: 50px;padding:5px 30px;">
		<table>
			<tr>
				<td style="width:65px"><label>词条：</label></td>
                <td style="width:140px"><input id="WORDS" class="easyui-combobox"></input></td>
                
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
		$('#btnQuery').bind("click",query);
		initCombobox("WORDS", "setting/words/select.do", undefined, false,"TYPE_ID","TYPE_NAME");
		query();
	});

	/**
	 * 执行查询
	 */
	function query() {
		var containId = "divStatWordsContent",typeId = $('#WORDS').combobox("getValue"),
		params={key : key,rows:100};
		if ('*' != typeId){
			params.TYPE_ID= typeId;
		}
		$('#' + containId).html('');
		showLoading(containId);
		loadData(containId, "crawl/query.do", params, function(datas) {
			var ec = window.echarts;
			if (!datas) {
				return;
			}
			$('#' + containId).html('');
			$('#' + containId).append(
					'<div id="divMetric_' + containId
							+ '" style="width:100%;height:100%"></div>');
			var divMetric = $('#divMetric_' + containId);
			var pageSize = 15;
			divMetric.datagrid({
				title:'最新舆情',
				fitColumns : true,
				rownumbers : true,
				showHeader : true,
				singleSelect : true,
				remoteSort : false,
				//idField : "TYPE_ID",
				//sortName : "status",
				//sortOrder : "asc",
				//tools: "#toolZXYQ",
				pagination: pageSize < datas.length,
			    pageSize:pageSize,
			    pageList: getPageList(pageSize),
			    columns : [ [ {
					field : 'TYPE_NAME',
					title : '词条',
					align : 'left',
					halign : 'center',
					sortable : "true"
				}, {
					field : 'SITE_NAME',
					title : '站点',
					align : 'center',
					halign : 'center'
				}, {
					field : 'TEMPLATE_TYPE',
					title : '所属模板',
					align : 'center',
					halign : 'center',
					formatter: function(value,row){
						if (value == 'ZM'){
							return '正面';
						}else if (value == 'ZM_E'){
							return '正面（英文）';
						}else if (value == 'FM'){
							return '负面';
						}else if (value == 'FM_E'){
							return '负面（英文）';
						}else{
							return '未定义';
						}
					}
				}, {
					field : 'TITLE',
					title : '标题',
					align : 'left',
					halign : 'center',
					formatter: function(value,row){
						return '<a href="' + row.URL + '" target="_blank" title="' + value + '" style="display:block;overflow:hidden; text-overflow:ellipsis;">' + value + '</a>';
					}
				}, {
					field : 'URL',
					title : 'URL',
					align : 'left',
					halign : 'center',
					formatter: function(value,row){
						return '<a href="' + row.URL + '" target="_blank" title="' + value + '" style="display:block;overflow:hidden; text-overflow:ellipsis;">' + value + '</a>';
					}
				} ] ],
				data : datas
			}).datagrid('clientPaging');
		});
	}
	
</script>
</html>
