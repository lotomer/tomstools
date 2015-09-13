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
	#divStatMainContent>div{border: 0px solid red;}
	.pnlHeader{background:none;}
	.msg{text-align:center;float: left;font-size: 14px;display: inline;margin-right:10px;height:25px;   
  line-height:25px; 
  overflow:hidden;   }
	.msgTitle,.panel-title{font-size:12px;font-weight: bold;padding:0 10px;color: #333;height:20px;
	text-align: left;background-color: #ccc}
	.msg>div{padding:0 2px;float:left;}
	.msgRow{width:99%;display:inline-block;}
	.moduleContent{padding:0 10px;}
	.module{float:left;float:left;margin:10px 0px 5px 15px;background-color: white;}
	.msgRow{margin: 15px 0 10px 10px;}
	.row1 {height: 145px;}
	.row2 {height: 180px;}
	.row3 {height: 130px;}
	.row1>.moduleContent{height: 125px;}
	.row2>.moduleContent{height: 160px;}
	.row3>.moduleContent{height: 110px;}
	A {text-decoration: NONE} 
	#divYQYJ A{margin-left: 10px;}
	.datagrid-row{border-width: 1px;border-style: dotted;}
	.datagrid-header td, .datagrid-body td, .datagrid-footer td {
	    border-width: 0px 0px 1px 0px;
	}
</style>
</head>
<body class="easyui-layout">
	<div id="divStatMainContent" style="background-color: #888" data-options="region:'center',split:true">
	    <div style="width: 260px;" class="module row1">
	    	<div class="msgTitle">舆情级别</div>
	    	<div id="divMain" class="moduleContent"></div>
	    </div>
   		<div id="divJRYQ" style="width: 250px" class="module row1">
   			<div class="msgTitle">今日舆情</div>
   			<div class="msgRow">
    			<div class="msg" style="width:50px;background-color: #228b22;color: white;">正面</div>
    			<div class="msg" style="width:140px;"><div>舆情</div><div id="divJRYQ_ZM" style="width:75px;text-align:center;font-size:20px;color: #228b22;">0</div><div>篇</div></div>
   			</div>
   			<div class="msgRow">
    			<div class="msg" style="width:50px;background-color: #ff4500;clear:left;color: white">负面</div>
    			<div class="msg" style="width:140px;"><div>舆情</div><div id="divJRYQ_FM" style="width:75px;text-align:center;font-size:20px;color: #ff4500;">0</div><div>篇</div></div>
   			</div>
   		</div>
   		<div id="divZRYQ" style="width: 310px" class="module row1">
   			<div class="msgTitle">昨日舆情</div>
   			<div class="msgRow">
   				<div class="msg" style="width:280px;text-align: left;"><div>文章总数：</div><div id="divZRYQ_TOTAL" style="color: #48b;width:60px;text-align:center">0</div><div>疑似负面：</div><div id="divZRYQ_FM" style="color: red;width:44px;text-align:center">0</div></div>
   			</div>
   			<div class="msgRow" >
    			<div class="msg" style="width:80px;clear:left;margin: 0px 10px 0px 40px;font-size:18px;text-align: left;">美誉度</div>
    			<div class="msg" style="width:80px;color: white;font-size:16px;height: 30px;line-height: 30px;" id="divZRYQ_TIPS"></div>
   			</div>
   		</div>
   		<div id="divYQYJ" class="module row1" style="width:231px;">
   			<div class="msgTitle">词条预警</div>
   			<div class="msg" id="divCTYQ" style="text-align: left;margin-top:10px;"></div>
   		</div>
   		
   		<div style="width: 260px;" class="module row2"><div class="msgTitle">舆情媒体类型</div><div id="divYQMTLX" class="moduleContent"></div></div>
   		<div style="width: 445px" class="module row2"><div class="msgTitle">舆情来源网站</div><div id="divSiteTop" class="moduleContent"></div></div>
   		<div style="width: 360px;" class="module row2"><div class="msgTitle">最新舆情<a href="javascript:void(0)" style="width:50px;float:right;" onclick="javascript:window.top.createPageById(102002)">更多...</a></div><div id="divZXYQ" class="moduleContent"></div></div>
   		
   		<div style="width: 720px; " class="module row3"><div class="msgTitle">舆情走势</div><div id="divYQZS" class="moduleContent"></div></div>
   		<div style="width: 360px;" class="module row3"><div class="msgTitle">舆情分布</div><div id="divXZFB" class="moduleContent"></div></div>
	</div>
	
	<div id="toolZXYQ">
        <a href="javascript:void(0)" style="width:50px" onclick="javascript:window.top.createPageById(102002)">更多...</a>
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
			: 5,COLOR_GOOD='#228b22',COLOR_NORMAL='#48b',COLOR_BAK='#ff4500',BACKGROUND_COLOR='#f8f8f8',TEXT_STYLE={fontSize:15,color:'black'};
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
			return {title:'很好',color:COLOR_GOOD}; 
		}else if (value < 20){
			return {title:'很差',color:COLOR_BAK};
		}else{
			return {title:'一般',color:COLOR_NORMAL};
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
		loadYQYJ();
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
					//x : "left",
					//text : '舆情级别',textStyle:TEXT_STYLE,backgroundColor:BACKGROUND_COLOR
				},
    		    series : [
    		        {
    		            name:'健康程度',
    		            type:'gauge',
    		            center:['50%','50%'],  // 圆心坐标，支持绝对值（px）和百分比，百分比计算min(width, height) * 50%
    		            radius:[0, '90%'],     // 半径，支持绝对值（px）和百分比，百分比计算比，min(width, height) / 2 * 75%，  传数组实现环形图，[内半径，外半径]
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
    		            detail : {
    		            	textStyle: {
		   		                color: 'auto',
		   		                fontSize : 18
		   		            },
    		            	formatter:function(value,row){
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
					var ec = window.echarts,colors=[COLOR_GOOD,COLOR_NORMAL,COLOR_BAK],colorIndex=0;
					$('#' + containId)
							.append(
									'<div id="divMetric_'
											+ containId
											+ '" style="width:100%;height:100%"></div>');
					var divMetric = $('#divMetric_' + containId), titles = [  ], values = {
						name : "信息量",
						type : "pie",
			            radius : '50%',
			            center: ['50%', '60%'],itemStyle : {
			                normal : {
			                    labelLine : {
			                        show : true,
			                        length : 1
			                    },
			                    label : {
			                        show : true,
			                        //position : 'inner',
			                        //formatter : '{b}({d}%)',
			                        formatter : '{d}%',
			                        textStyle : {
			                            //color : 'white'
			                        }
			                    }
			                },
			                emphasis : {
			                    labelLine : {
			                        show : true,
			                        length : 1
			                    },
			                    label : {
			                        show : true,
			                        //position : 'inner',
			                        //formatter : '{b}({d}%)',
			                        formatter : '{d}%',
			                        textStyle : {
			                            //color : 'white'
			                        }
			                    }
			                }
			            },
						data : []
					};
					for (var i = 0, oLen = datas.length; i < oLen; i++) {
						var aData = datas[i],o={value:aData.SIZE,name:aData.SITE_TYPE_NAME};
						titles.push(aData.SITE_TYPE_NAME);
						if (colorIndex < colors.length){
							o.itemStyle = {normal:{color:colors[colorIndex++]}};
						}
						values.data.push(o);
					}
					var option = {
						title : {
							//x : "left",
							//text : '舆情媒体类型',
							//subtext : '最近7天  媒体类型对比图',textStyle:TEXT_STYLE
						},
						tooltip : {
					        trigger: 'item',
					        formatter: "{b}: {c} ({d}%)"
						},
						legend : {
							show : true,
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
						series : [ values ]
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
						type : "bar",barWidth:40,
						data : [],
						itemStyle : {
							normal : {
								color : COLOR_NORMAL,
								label : {
									show : true,
									position : 'top',
									textStyle : {
										color : "black"
									}
								}
							},emphasis : {
								color : COLOR_NORMAL,
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
						x.push(aData.SITE_NAME);
						size.data.push(aData.SIZE);
					}
					var option = {
						title : {
							//x : "left",
							//text : '舆情来源网站',textStyle:TEXT_STYLE,
							//subtext : '最近7天  来源网站TOP' + topNum
						},
						tooltip : {
							trigger : 'axis',
					        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
					            type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
					        }
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
							x : 40,
							y : 30,
							x2 : 20,
							y2 : 25
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
			var pageSize = 5;
			divMetric.datagrid({
				//title:'新增分布',
				fitColumns : false,
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
					title : '',
					align : 'center',
					halign : 'center'
				}, {
					field : '1d',
					title : '今天',
					align : 'center',width:50,
					halign : 'center'
				}, {
					field : '7d',
					title : '最近七天',
					align : 'center',width:65,
					halign : 'center'
				} , {
					field : '1m',
					title : '最近一个月',
					align : 'center',width:65,
					halign : 'center'
				} , {
					field : '1y',
					title : '最近一年',
					align : 'center',width:78,
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
			rows: 5
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
			var pageSize = 5;
			divMetric.datagrid({
				//title:'最新舆情',
				fitColumns : false,
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
					halign : 'left',width:264,
					formatter : function(value, row) {
						return '<a href="' + row.URL + '" target="_blank" title="' + value + '" style="display:block;overflow:hidden; text-overflow:ellipsis;">' + value + '</a>';
					}
				}, {
					field : 'CRAWL_TIME',
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
						if (-1 == arrayIndexOf(x,aData.dt)) {
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
							//text : '舆情走势',textStyle:TEXT_STYLE
						//subtext: '纯属虚构'
						},
						tooltip : {
							trigger : 'axis'
						},
						legend : {
							data : titles,
							x : "center"
						},grid : {
				            x : 50,
				            y : 20,
				            x2 : 20,
				            y2 : 20
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
	function showYQ(containId,words){
		$('#' + containId).append('<a href="#" onclick="javascript:window.top.createPageById(103004)" value="' +words+ '"  style="width:100px;float:left;display:block;overflow:hidden; text-overflow:ellipsis;">' + words+ '</a>');
	}
	// 舆情预警
	function loadYQYJ() {
		var containId = "divCTYQ", TopN = 10;
		
		loadData(containId, "crawl/stat/wordsAlertTop.do", {
			key : key,
			topNum : TopN
		}, function(datas) {
			if (!datas) {
				return;
			}

			$('#' + containId).html('');
			for (var i = 0, oLen = datas.length; i < oLen; i++) {
				var aData = datas[i].METRICS.split('$$$');
				if (aData.length > 1){
					var wordsList = aData[1].split(',');
					for(var j = 0,jLen = wordsList.length; j < jLen;j++){
						if (j < TopN){
							showYQ(containId,wordsList[j]);
						}
					}
				}
			}
		});
	}
</script>
</html>
