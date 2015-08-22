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
		function initEcharts(ec) {
			window.echarts = ec;
			query();
		}
		// 路径配置
		require.config({
			paths : {
				echarts : 'js/echarts'
			}
		});
		// 使用
		require([ 'echarts', 'echarts/chart/pie', // 按需加载
		'echarts/chart/line', // 按需加载
		'echarts/chart/bar', // 按需加载
		'echarts/chart/gauge'
		], initEcharts);
	});

	/**
	 * 执行查询
	 */
	function query() {
		var containId = "divStatWordsContent";
		$('#' + containId).html('');
		showLoading(containId);
		loadData(containId, "crawl/query.do", {
			key : key,
			rows:100
		}, function(datas) {
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
				rownumbers : false,
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
					field : 'title',
					title : '标题',
					align : 'left',
					halign : 'left',width:280,
					formatter : function(value, row) {
						return '<a href="' + row.url + '" target="_blank" title="' + value + '" style="display:block;overflow:hidden; text-overflow:ellipsis;">' + value + '</a>';
					}
				}, {
					field : 'tstamp',
					title : '时间',
					align : 'center',
					halign : 'center'
				} ] ],
				data : datas
			}).datagrid('clientPaging');
		});
	}
	
</script>
</html>
