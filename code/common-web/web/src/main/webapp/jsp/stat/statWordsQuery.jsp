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
		style="height: 80px;padding:5px 30px;">
		<table>
			<tr>
                <!-- <td style="width:65px"><label>监控词汇 ：</label></td>
                <td style="width:140px"><input id="inputWords" class="easyui-textbox"></input></td> -->
                <td style="width:65px"><label>语言：</label></td>
                <td style="width:140px"><input id="selLang" class="easyui-combobox"></input></td>
                <td style="width:65px"><label>区域：</label></td>
                <td style="width:140px"><input id="selCountry" class="easyui-combobox"></input></td>
                <td style="width:65px"><label>站点类型：</label></td>
                <td style="width:140px"><input id="selSiteType" class="easyui-combobox"></input></td>
                <td style="width:65px"><label>站点：</label></td>
                <td style="width:140px"><input id="selSite" class="easyui-combobox"></input></td>
            </tr>
			<tr>
				<td><label>开始时间：</label></td>
				<td><input id="st" class="easyui-datetimebox"
					data-options="showSeconds:false"></td>
				<td><label>结束时间：</label></td>
				<td><input id="et" class="easyui-datetimebox"
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
		initCombobox("selLang","crawl/lang.do");
		initCombobox("selCountry","crawl/country.do");
		initCombobox("selSiteType","crawl/selectSiteType.do",function(record){
			if (record.id == '*'){
				initCombobox("selSite","crawl/selectSite.do");
			}else{
				initCombobox("selSite","crawl/selectSite.do?siteTypeId=" + record.id);
			}
		});
		initCombobox("selSite","crawl/selectSite.do");
		query();
	});
	
	
	/**
	 * 执行查询
	 */
	function query() {
		// 获取查询条件
		var startTime = $('#st').datetimebox('getValue'), // 开始时间
		endTime = $('#et').datetimebox('getValue'), // 结束时间
		lang = $('#selLang').datetimebox('getValue'), // 语言
		country = $('#selCountry').datetimebox('getValue'), // 国家
		siteTypeId = $('#selSiteType').datetimebox('getValue'), // 站点类型
		siteId = $('#selSite').datetimebox('getValue'); // 站点
		$('#divStatWordsContent').html('');
		showLoading("divStatWordsContent");
		loadData(startTime, endTime,lang,country,siteTypeId,siteId,processData);
	}
	function loadData(startTime, endTime,lang,country,siteTypeId,siteId,successCallback) {
		var params = {
				key : key,
				startTime : startTime,
				endTime : endTime
			};
		if (lang && '*' != lang){
			params.langId = lang;
		}
		if (country && '*' != country){
			params.countryId = country;
		}
		if (siteTypeId && '*' != siteTypeId){
			params.siteTypeId = siteTypeId;
		}
		if (siteId && '*' != siteId){
			params.siteId = siteId;
		}
		$.ajax({
			url : "crawl/stat/wordsCountQuery.do",
			dataType : 'json',
			async : true,
			data : params,
			success : successCallback
		});
	}
	function processData(datas) {
		var divMetric = $('#divStatWordsContent');
		$('#divStatWordsContent').html('');
		if (!datas) {
			return;
		}
		$('#divStatWordsContent').append('<div id="divMetric" style="width:100%;height:100%"></div>');
		var divMetric = $('#divMetric');
		var pageSize = 5;
		divMetric.datagrid({
			//title:'${title}',
			fitColumns : true,
			rownumbers : true,
			singleSelect : true,
			remoteSort : false,
			idField : "TYPE_ID",
			//sortName : "status",
			//sortOrder : "asc",
			pagination : pageSize < datas.length,
			pageSize : pageSize,
			pageList : getPageList(pageSize),
			columns : [ [ {
				field : 'TYPE_NAME',
				title : '词条',
				align : 'center',
				halign : 'center',
				sortable : "true"
			}, {
				field : 'SIZE_ZM',
				title : '正面信息数',
				align : 'center',
				halign : 'center',
				formatter: function(value,row){
					return row.SIZE_ZM + row.SIZE_ZM_E;
				}
			}, {
				field : 'SIZE_FM',
				title : '负面信息数',
				align : 'center',
				halign : 'center',
				formatter: function(value,row){
					return row.SIZE_FM + row.SIZE_FM_E;
				}
			} ] ],
			data : datas
		}).datagrid('clientPaging');
	}
</script>
</html>
