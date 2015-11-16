<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>词条数统计</title>
<link rel="stylesheet" type="text/css"
	href='css/easyui/themes/${theme}/easyui.css'>
<link rel="stylesheet" type="text/css" href="css/easyui/themes/icon.css">
<link rel="stylesheet" type="text/css" href="css/main.css">
</head>
<body class="easyui-layout">
	<div data-options="region:'north',split:true"
		style="height: 60px; padding: 5px 30px;">
		<table>
			<tr>
				<td><label>开始时间：</label></td>
				<td><input id="st" class="easyui-datebox"
					data-options="showSeconds:false"></td>
				<td><label>结束时间：</label></td>
				<td><input id="et" class="easyui-datebox"
					data-options="showSeconds:false"></td>
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
		endTime = $('#et').datetimebox('getValue'); // 开始时间
		$('#divStatWordsContent').html('');
		showLoading("divStatWordsContent");
		loadData(startTime, endTime);
	}
	function loadData(startTime, endTime) {
		$.ajax({
			url : "crawl/stat/words.do",
			dataType : 'json',
			async : true,
			data : {
				key : key,
				startTime : startTime,
				endTime : endTime
			},
			success : function(data) {
				processData(data);
			}
		});
	}
	function processData(datas) {
		if (!datas || !datas.length) {
			showEmpty('divStatWordsContent');
			return;
		}
		var ec=window.echarts;
		$('#divStatWordsContent').html('');
		$('#divStatWordsContent').append(
				'<div id="divMetric" style="width:100%;height:100%"></div>');
		var divMetric = $('#divMetric'),xData = {},titles=[],x=[],values=[];
		for (var i = 0, oLen = datas.length; i < oLen; i++) {
			var aData = datas[i];
			// 添加到x坐标数据
			if (-1 == arrayIndexOf(x,aData.dt)){
				x.push(aData.dt);
			}
			if (!xData[aData.TYPE_NAME]){
				xData[aData.TYPE_NAME]={data:[]};
			}
			xData[aData.TYPE_NAME][aData.dt] = aData;
		}
		// x坐标数据排序
		x.sort();
		
		// 生成图表数据
		for ( var name in xData) {
			titles.push(name);
			var aData= xData[name];
			// 根据标题获取数据
			for (var j = 0; j < x.length; j++) {
				if (!aData[x[j]]){
					// 没有对应横坐标（时间）的数据
					aData.data.push("-");
				}else{
					aData.data.push(aData[x[j]].SIZE_ZM + aData[x[j]].SIZE_FM + aData[x[j]].SIZE_ZM_E + aData[x[j]].SIZE_FM_E);
				}
			}
			values.push({name:name,type:"line",stack:"a",data:aData.data,itemStyle: {normal: {areaStyle: {type: 'default'}}}});
		}
		var option = {
			title : {
			//text: '某地区蒸发量和降水量',
			//subtext: '纯属虚构'
			},
			tooltip : {
				trigger : 'axis'
			},
			legend : {
				data : titles,
				x:"left"
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
			series : values
		};
		var mychart = doCreateChart(ec, divMetric.attr("id"), option);
		$(window).resize(function() {
			mychart.resize();
		});
		
	}
</script>
</html>
