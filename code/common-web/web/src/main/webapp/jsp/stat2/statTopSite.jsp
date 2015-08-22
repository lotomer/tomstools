<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>舆情来源网站</title>
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
	var theme = '${theme}', key = '${user.key}', topNum = '${topNum}'?'${topNum}' : 5;
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
			key : key,
			topNum : topNum
		};
		$.ajax({
			url : "crawl/stat/siteTop.do",
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

		var divMetric = $('#divMetric'), titles = [ "信息量" ], size = {
			name : titles[0],
			type : "bar",
			data : [],
			itemStyle : {
				normal : {
					color : "blue",
					label : {
						show : true,
						position : 'insideTop',
						textStyle : {
							color : "black"
						}
					}
				}
			}
		}, x = [];
		for (var i = 0, oLen = datas.length; i < oLen; i++) {
			var aData = datas[i];
			x.push(aData.SITE_NAME);
			size.data.push(aData.SIZE);
		}
		var option = {
			title : {
				x:"center",
				text : '舆情来源网站',
				subtext : '最近7天  来源网站TOP' + topNum
			},
			tooltip : {
				trigger : 'axis'
			},
			legend : {
				show : false,
				data : titles
			},
			toolbox : {
				show : false,
				feature : {
					mark : {
						show : true
					},
					dataView : {
						show : true,
						readOnly : false
					},
					magicType : {
						show : true,
						type : [ 'line', 'bar', 'stack', 'tiled' ]
					},
					restore : {
						show : true
					},
					saveAsImage : {
						show : true
					}
				}
			},grid : {
	            x : 60,
	            y : 45,
	            x2 : 20,
	            y2 : 45
	        },
			calculable : true,
			xAxis : [ {
				type : 'category',
				data : x
			} ],
			yAxis : [ {
				type : 'value'
			} ],
			series : [ size ]
		};
		var mychart = doCreateChart(ec, divMetric.attr("id"), option);
		//$(window).resize(function() {
		//	mychart.resize();
		//});
	}
</script>
</html>
