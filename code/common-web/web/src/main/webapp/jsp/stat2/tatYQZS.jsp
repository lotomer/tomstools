<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>今日舆情</title>
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
		$('#divStatWordsContent').html('');
		showLoading("divStatWordsContent");
		loadData(processData);
	}
	function loadData(successCallback) {
		var params = {
			key : key
		};
		$.ajax({
			url : "crawl/stat/today.do",
			dataType : 'json',
			async : true,
			data : params,
			success : successCallback
		});
	}
	function processData(datas) {
		var ec = window.echarts;
		if (!datas) {
			return;
		}
		$('#divStatWordsContent').html('');
		$('#divStatWordsContent').append(
				'<div id="divMetric" style="width:100%;height:100%"></div>');

		var divMetric = $('#divMetric'), zm = 0, fm = 0;
		for (var i = 0, oLen = datas.length; i < oLen; i++) {
			var aData = datas[i];
			zm = zm + aData.SIZE_ZM + aData.SIZE_ZM_E;
			fm = fm + aData.SIZE_FM + aData.SIZE_FM_E;
		}
		var pageSize = 5;
		divMetric.datagrid({
			//title:'${title}',
			fitColumns : true,
			rownumbers : false,
			showHeader:false,
			singleSelect : true,
			remoteSort : false,
			idField : "TYPE_ID",
			//sortName : "status",
			//sortOrder : "asc",
			columns : [ [ {
				field : 'c1',
				title : 'title',
				align : 'center',
				halign : 'center',
				style: function(value,row){
					return 'black-color:red';
				}
			}, {
				field : 'c2',
				title : '负面信息数',
				align : 'center',
				halign : 'center',
				formatter: function(value,row){
					return '舆情' + value + '篇';
				}
			} ] ],
			data : [{c1:"正面",c2:zm},{c1:"负面",c2:fm}]
		}).datagrid('clientPaging');
	}
</script>
</html>
