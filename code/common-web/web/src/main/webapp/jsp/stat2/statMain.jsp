<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>首页</title>
<link rel="stylesheet" type="text/css"
	href='css/easyui/themes/${theme}/easyui.css'>
<link rel="stylesheet" type="text/css" href="css/easyui/themes/icon.css">
<link rel="stylesheet" type="text/css" href="css/main.css">
<style type="text/css">
	#divStatMainContent>div{border: 0px solid red;padding:2px 5px 5px 2px;}
	.pnlHeader{background:none;}
	.msg{
	height:30px;text-align:center;float: left;font-size: 16px;display: inline;
	}
	.msgTitle{font-size:20px;font-weight: bold;color: #333}
	.msg>label{padding:0 5px;}
</style>
</head>
<body class="easyui-layout">
	<div id="divStatMainContent" style="padding-left:10px;" data-options="region:'center',split:true">
		<div id="divMain" style="width: 200px; height: 185px; float: left"></div>
		<div id="divJRYQ" style="width: 220px; height: 185px; float: left">
			<div class="msg msgTitle" style="width:220px;">今日舆情</div>
			<div class="msg" style="width:60px;background-color: #228b22;color: white;">正面</div>
			<div class="msg" style="width:160px;text-align: left;background-color: #f2f2f2;"><label style="float: left;">舆情</label><span id="divJRYQ_ZM" style="width:60px;text-align:center;font-size:20px;color: #228b22">0</span><label style="padding-right:20px;float: right;">篇</label></div>
			<div class="msg" style="width:60px;background-color: #ff4500;clear:left;color: white">负面</div>
			<div class="msg" style="width:160px;text-align: left;background-color: #f2f2f2;"><label style="float: left;">舆情</label><span id="divJRYQ_FM" style="width:60px;text-align:center;font-size:20px;color: #ff4500">0</span><label style="padding-right:20px;float: right;">篇</label></div>
		</div>
		<div id="divYQMTLX" style="width: 350px; height: 185px; float: left"></div>
		<div id="divSiteTop" style="width: 400px; height: 185px; float: left"></div>
		
		<div id="divZRYQ" style="width: 300px; height: 155px; background-color: #f2f2f2;float: left;padding:0 20px;">
			<div class="msg msgTitle" style="width:280px;">昨日舆情</div>
			<div class="msg" style="width:280px;text-align: left;">文章总数：<span id="divZRYQ_TOTAL" style="color: #48b">0</span>&nbsp;&nbsp;&nbsp;疑似负面：<span id="divZRYQ_FM" style="color: red">0</span></div>
			<div class="msg" style="width:80px;clear:left;margin-right: 40px;font-size:20px;text-align: left;">美誉度</div>
			<div class="msg" style="width:80px;color: white;font-size:20px;" id="divZRYQ_TIPS"></div>
		</div>
		<div id="divXZFB" style="width: 360px; height: 155px; float: left;"></div>
		<div id="divZXYQ" style="width: 460px; height: 155px; float: left"></div>
		
		<div id="divYQZS" style="width: 1190px; height: 155px; float: left"></div>
	</div>
	
	<div id="toolZXYQ">
        <a href="javascript:void(0)" style="width:50px" onclick="javascript:window.top.createPageById(201001)">更多...</a>
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
	var showBorder = false,theme = '${theme}', key = '${user.key}', topNum = '${topNum}' ? '${topNum}'
			: 5;
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
	function getValue(zm,fm){
		var sizeZM = 1,sizeFM=0;
		if (zm){
			sizeZM = zm;
		}
		if (fm){
			sizeFM = fm;
		}
		return toDecimal((100 * sizeZM)/(sizeZM+sizeFM),0);
	}
	function getTips(value){
		if (value > 80){
			return {title:'很好',color:'#228b22'}; 
		}else if (value < 20){
			return {title:'很差',color:'#ff4500'};
		}else{
			return {title:'一般',color:'#48b'};
		}
	}
	/**
	 * 执行查询
	 */
	function query() {
		$('#divMain').html('');
		loadJRYQ();
		loadYQMTLX();
		loadSiteTop();
		loadZRYQ();
		loadXZFB();
		loadZXYQ();
		loadYQZS();
	}
	function loadMain(ec,zm,fm){
		var containId = "divMain";
		$('#' + containId).html('');
		$('#' + containId).append(
				'<div id="divMetric_' + containId
						+ '" style="width:100%;height:100%"></div>');
		//log(toDecimal((100 * zm)/total,0));
		var divMetric = $('#divMetric_' + containId);
		var option = {
				title : {
					x : "center",
					text : '舆情级别'
				},
    		    series : [
    		        {
    		            name:'健康程度',
    		            type:'gauge',
    		            center:['50%','55%'],  // 圆心坐标，支持绝对值（px）和百分比，百分比计算min(width, height) * 50%
    		            radius:[0, '75%'],     // 半径，支持绝对值（px）和百分比，百分比计算比，min(width, height) / 2 * 75%，  传数组实现环形图，[内半径，外半径]
    		            axisLine: {            // 坐标轴线
    		                lineStyle: {       // 属性lineStyle控制线条样式
    		                    color: [[0.2, '#ff4500'],[0.8, '#48b'],[1, '#228b22']], 
    		                    width: 20
    		                }
    		            },
    		            axisTick: {            // 坐标轴小标记
    		            	show: true,
    		            	splitNumber: 10,   // 每份split细分多少段
    		                length :20,        // 属性length控制线长
    		                lineStyle: {       // 属性lineStyle控制线条样式
    		                    color: 'auto'
    		                }
    		            },
    		            axisLabel: {           // 坐标轴文本标签，详见axis.axisLabel
    		            	show: false,
    		            	textStyle: {       // 其余属性默认使用全局文本样式，详见TEXTSTYLE
    		                    color: 'auto'
    		                }
    		            },
    		            splitLine: {           // 分隔线
    		                show: true,        // 默认显示，属性show控制显示与否
    		                length :30,         // 属性length控制线长
    		                lineStyle: {       // 属性lineStyle（详见lineStyle）控制线条样式
    		                    color: 'auto'
    		                }
    		            },
    		            pointer : {
    		                width : 5
    		            },
    		            title : {
    		                show : true,
    		                offsetCenter: [0, '-40%'],       // x, y，单位px
    		                textStyle: {       // 其余属性默认使用全局文本样式，详见TEXTSTYLE
    		                    //fontWeight: 'bolder'
    		                }
    		            },
    		            detail : {formatter:function(value,row){
    		            		return getTips(value).title;
    		            	}
    		            },
    		            data:[{value: getValue(zm,fm)}]
    		        }
    		    ]
    		};
        mychart = doCreateChart(ec,divMetric.attr("id"),option);
    	$(window).resize(function(){
    	    mychart.resize(); 
    	});
	}
	// 今日舆情
	function loadJRYQ() {
		var containId = "divJRYQ";
		loadData(containId, "crawl/stat/today.do", {
			key : key
		}, function(datas) {
			if (!datas) {
				return;
			}
			var ec = window.echarts;
			var zm = 0, fm = 0;
			for (var i = 0, oLen = datas.length; i < oLen; i++) {
				var aData = datas[i];
				zm = zm + aData.SIZE_ZM + aData.SIZE_ZM_E;
				fm = fm + aData.SIZE_FM + aData.SIZE_FM_E;
			}
			loadMain(ec,zm,fm);
			$("#divJRYQ_ZM").text(zm);
			$("#divJRYQ_FM").text(fm);
		});
	}
	// 舆情媒体类型
	function loadYQMTLX() {
		var containId = "divYQMTLX";
		$('#' + containId).html('');
		showLoading(containId);
		loadData(
				containId,
				"crawl/stat/mediaCount.do",
				{
					key : key
				},
				function(datas) {
					$('#' + containId).html('');
					if (!datas) {
						return;
					}
					var ec = window.echarts;
					$('#' + containId)
							.append(
									'<div id="divMetric_'
											+ containId
											+ '" style="width:100%;height:100%"></div>');
					var divMetric = $('#divMetric_' + containId), titles = [  ], value = {
						name : "信息量",
						type : "pie",
			            radius : '55%',
			            center: ['50%', '60%'],
						data : []
					};
					for (var i = 0, oLen = datas.length; i < oLen; i++) {
						var aData = datas[i];
						titles.push(aData.SITE_TYPE_NAME);
						value.data.push({value:aData.SIZE,name:aData.SITE_TYPE_NAME});
					}
					var option = {
						title : {
							x : "center",
							text : '舆情媒体类型',
							subtext : '最近7天  媒体类型对比图'
						},
						tooltip : {
					        trigger: 'item',
					        formatter: "{a} <br/>{b} : {c} ({d}%)"
						},
						legend : {
							show : false,
							data : titles
						},
					    toolbox: {
					        show : false,
					        feature : {
					            mark : {show: true},
					            dataView : {show: true, readOnly: false},
					            magicType : {
					                show: true, 
					                type: ['pie', 'funnel'],
					                option: {
					                    funnel: {
					                        x: '25%',
					                        width: '50%',
					                        funnelAlign: 'left',
					                        max: 1548
					                    }
					                }
					            },
					            restore : {show: true},
					            saveAsImage : {show: true}
					        }
					    },
						calculable : true,
						series : [ value ]
					};
					var mychart = doCreateChart(ec, divMetric.attr("id"),
							option);
					//$(window).resize(function() {
					//	mychart.resize();
					//});
				});
	}
	// 来源网站
	function loadSiteTop() {
		var containId = "divSiteTop";
		$('#' + containId).html('');
		showLoading(containId);
		loadData(
				containId,
				"crawl/stat/siteTop.do",
				{
					key : key,
					topNum : topNum
				},
				function(datas) {
					$('#' + containId).html('');
					if (!datas) {
						return;
					}
					var ec = window.echarts;
					$('#' + containId)
							.append(
									'<div id="divMetric_'
											+ containId
											+ '" style="width:100%;height:100%"></div>');
					var divMetric = $('#divMetric_' + containId), titles = [ "信息量" ], size = {
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
							x : "center",
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
						},
						grid : {
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
					var mychart = doCreateChart(ec, divMetric.attr("id"),
							option);
					//$(window).resize(function() {
					//	mychart.resize();
					//});
				});
	}
	// 昨日舆情
	function loadZRYQ() {
		var containId = 'divZRYQ';
		loadData(containId, "crawl/stat/yesterday.do", {
			key : key
		}, function(datas) {
			var ec = window.echarts;
			if (!datas) {
				return;
			}
			var zm = 0, fm = 0;
			for (var i = 0, oLen = datas.length; i < oLen; i++) {
				var aData = datas[i];
				zm = zm + aData.SIZE_ZM + aData.SIZE_ZM_E;
				fm = fm + aData.SIZE_FM + aData.SIZE_FM_E;
			}
			$("#divZRYQ_TOTAL").text(zm+fm);
			$("#divZRYQ_FM").text(fm);
			var o = getTips(getValue(zm,fm));
			$("#divZRYQ_TIPS").text(o.title);
			$("#divZRYQ_TIPS").css("background-color",o.color);
		});
	}
	// 新增分布
	function loadXZFB() {
		var containId = "divXZFB";
		$('#' + containId).html('');
		showLoading(containId);
		loadData(containId, "crawl/stat/increment.do", {
			key : key
		}, function(datas) {
			$('#' + containId).html('');
			if (!datas) {
				return;
			}
			var ec = window.echarts;
			$('#' + containId).append(
					'<div id="divMetric_' + containId
							+ '" style="width:100%;height:100%"></div>');
			var divMetric = $('#divMetric_' + containId);
			log(datas);
			var pageSize = 5;
			divMetric.datagrid({
				//title:'新增分布',
				fitColumns : true,
				headerCls:"pnlHeader",
				rownumbers : false,
				showHeader : true,
				singleSelect : true,
				remoteSort : false,
				border: showBorder,
				//idField : "TYPE_ID",
				//sortName : "status",
				//sortOrder : "asc",
				columns : [ [ {
					field : 'name',
					title : '新增分布',
					align : 'center',
					halign : 'center'
				}, {
					field : '1d',
					title : '今天',
					align : 'center',
					halign : 'center'
				}, {
					field : '7d',
					title : '最近七天',
					align : 'center',
					halign : 'center'
				} , {
					field : '1m',
					title : '最近一个月',
					align : 'center',
					halign : 'center'
				} , {
					field : '1y',
					title : '最近一年',
					align : 'center',
					halign : 'center'
				} ] ],
				data : datas
			}).datagrid('clientPaging');
		});
	}
	// 最新舆情
	function loadZXYQ() {
		var containId = "divZXYQ";
		$('#' + containId).html('');
		showLoading(containId);
		loadData(containId, "crawl/query.do", {
			key : key,
			rows: 4
		}, function(datas) {
			$('#' + containId).html('');
			if (!datas) {
				return;
			}
			log(datas);
			var ec = window.echarts;
			$('#' + containId).append(
					'<div id="divMetric_' + containId
							+ '" style="width:100%;height:100%"></div>');
			var divMetric = $('#divMetric_' + containId);
			var pageSize = 5;
			divMetric.datagrid({
				title:'最新舆情',
				fitColumns : true,
				headerCls:"pnlHeader",
				rownumbers : false,
				showHeader : true,
				singleSelect : true,
				remoteSort : false,
				border: showBorder,
				//idField : "TYPE_ID",
				//sortName : "status",
				//sortOrder : "asc",
				tools: "#toolZXYQ",
				columns : [ [ {
					field : 'TITLE',
					title : '标题',
					align : 'left',
					halign : 'left',width:280,
					formatter : function(value, row) {
						return '<a href="' + row.URL + '" target="_blank" title="' + value + '" style="display:block;overflow:hidden; text-overflow:ellipsis;">' + value + '</a>';
					}
				}, {
					field : 'DT',
					title : '时间',
					align : 'center',
					halign : 'center'
				} ] ],
				data : datas
			}).datagrid('clientPaging');
		});
	}

	// 舆情走势
	function loadYQZS() {
		var containId = "divYQZS";
		$('#' + containId).html('');
		showLoading(containId);
		loadData(
				containId,
				"crawl/stat/media.do",
				{
					key : key
				},
				function(datas) {
					$('#' + containId).html('');
					if (!datas) {
						return;
					}
					var ec = window.echarts;
					$('#' + containId)
							.append(
									'<div id="divMetric_'
											+ containId
											+ '" style="width:100%;height:100%"></div>');
					var divMetric = $('#divMetric_' + containId), xData = {}, titles = [], x = [], values = [];
					for (var i = 0, oLen = datas.length; i < oLen; i++) {
						var aData = datas[i];
						// 添加到x坐标数据
						if (-1 == x.indexOf(aData.dt)) {
							x.push(aData.dt);
						}
						if (!xData[aData.SITE_TYPE_NAME]) {
							xData[aData.SITE_TYPE_NAME] = {
								data : []
							};
						}
						xData[aData.SITE_TYPE_NAME][aData.dt] = aData;
					}
					// x坐标数据排序
					x.sort();

					// 生成图表数据
					for ( var name in xData) {
						titles.push(name);
						var aData = xData[name];
						// 根据标题获取数据
						for (var j = 0; j < x.length; j++) {
							if (!aData[x[j]]) {
								// 没有对应横坐标（时间）的数据
								aData.data.push("-");
							} else {
								aData.data.push(aData[x[j]].SIZE);
							}
						}
						values.push({
							name : name,
							type : "line",
							stack : undefined,
							data : aData.data
						});
					}
					var option = {
						title : {
							text : '舆情走势'
						//subtext: '纯属虚构'
						},
						tooltip : {
							trigger : 'axis'
						},
						legend : {
							data : titles,
							x : "center"
						},grid : {
				            x : 60,
				            y : 40,
				            x2 : 20,
				            y2 : 35
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
					var mychart = doCreateChart(ec, divMetric.attr("id"),
							option);
					$(window).resize(function() {
						mychart.resize();
					});
				});
	}
</script>
</html>
