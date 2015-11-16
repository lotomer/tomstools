<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>词汇词条统计</title>
<link rel="stylesheet" type="text/css"
	href='css/easyui/themes/${theme}/easyui.css'>
<link rel="stylesheet" type="text/css" href="css/easyui/themes/icon.css">
<link rel="stylesheet" type="text/css" href="css/main.css">
</head>
<body class="easyui-layout">
	<div data-options="region:'north',split:true"
		style="height: 80px; padding: 5px 30px;">
		<table>
			<tr>
				<td><label>开始时间：</label></td>
				<td><input id="st" class="easyui-datebox"
					data-options="showSeconds:false"></td>
				<td><label>结束时间：</label></td>
				<td><input id="et" class="easyui-datebox"
					data-options="showSeconds:false"></td>
			</tr>
			<tr>
				<td style="width: 65px"><label>监控词汇 ：</label></td>
				<td style="width: 140px"><input id="inputWord"
					class="easyui-textbox"></input></td>
				<td></td>
				<td><a href="#" id="btnStat" class="easyui-linkbutton"
					data-options="iconCls:'icon-search'" style="width: 80px">统计</a></td>
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
		$('#btnStat').bind('click', query);
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
		'echarts/chart/bar' // 按需加载
		], initEcharts);
	});

	/**
	 * 执行查询
	 */
	function query() {
		// 获取查询条件
		var startTime = $('#st').datetimebox('getValue'), // 开始时间
		endTime = $('#et').datetimebox('getValue'), // 结束时间
		inputWord = $('#inputWord').val(); // 输入词汇
		$('#divStatWordsContent').html('');
		showLoading("divStatWordsContent");
		loadData(startTime, endTime, inputWord, processData);
	}
	function loadData(startTime, endTime, inputWord, successCallback) {
		var params = {
			key : key,
			startTime : startTime,
			endTime : endTime
		};
		if (inputWord && '*' != inputWord) {
			params.word = inputWord;
		}
		$.ajax({
			url : "crawl/stat/wordsWithWordCount.do",
			dataType : 'json',
			async : true,
			data : params,
			success : successCallback
		});
	}
	function processData(datas) {
		if (!datas || !datas.length) {
			showEmpty('divStatWordsContent');
			return;
		}
		var ec = window.echarts;
		$('#divStatWordsContent').html('');
		$('#divStatWordsContent').append(
				'<div id="divMetric" style="width:100%;height:100%"></div>');

		var divMetric = $('#divMetric'), titles = [ "正面信息量", "负面信息量" ], zm = {
			name : titles[0],
			type : "bar",barWidth:40,
			data : [],
			itemStyle : {
				normal : {
					color : "blue",
					label : {
						show : true,
						position : 'top',
						textStyle : {
							color : "black"
						}
					}
				}
			}
		}, fm = {
			name : titles[1],
			type : "bar",barWidth:40,
			data : [],
			itemStyle : {
				normal : {
					color : "red",
					label : {
						show : true,
						position : 'top',
						textStyle : {
							color : "black"
						}
					}
				}
			}
		}, x = [];
		for (var i = 0, oLen = datas.length; i < oLen; i++) {
			var aData = datas[i];
			x.push(aData.TYPE_NAME);
			zm.data.push(aData.SIZE_ZM + aData.SIZE_ZM_E);
			fm.data.push(aData.SIZE_FM + aData.SIZE_FM_E);
		}
		var option = {
			title : {
			//text: '某地区蒸发量和降水量',
			//subtext: '纯属虚构'
			},
			tooltip : {
				trigger : 'axis',
		        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
		            type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
		        }
			},
			legend : {
				data : titles
			},
			toolbox : {
				show : true,
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
			},
			calculable : true,
			xAxis : [ {
				type : 'category',
				data : x
			} ],
			yAxis : [ {
				type : 'value'
			} ],
			series : [ zm, fm ]
		};
		var mychart = doCreateChart(ec, divMetric.attr("id"), option);
		$(window).resize(function() {
			mychart.resize();
		});
	}
</script>
</html>
