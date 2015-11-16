<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>爬虫状态</title>
<link rel="stylesheet" type="text/css"
	href='css/easyui/themes/${theme}/easyui.css'>
<link rel="stylesheet" type="text/css" href="css/easyui/themes/icon.css">
<link rel="stylesheet" type="text/css" href="css/main.css">
<style type="text/css">
        #fm{
            margin:0;
            width: 100%;height: 100%
        }
        .ftitle{
            font-size:14px;
            font-weight:bold;
            padding:5px 0;
            margin-bottom:10px;
            border-bottom:1px solid #ccc;
        }
        .fitem{
            margin-bottom:5px;width: 100%;height: 100%
        }
        .fitem label{
            display:inline-block;
            width:140px;
        }
        .fitem textarea{
            width:100%;height: 100%
        }
    </style>
</head>
<body class="easyui-layout">
	<div id="divStatWordsContent" data-options="region:'center',split:true">
	</div><div id="dlg" class="easyui-dialog" data-options="modal:true" style="width:600px;height:400px;padding:10px 10px"
            closed="true" buttons="#dlg-buttons">
        <!-- <div class="ftitle">智能词条</div> -->
        <form id="fm" method="post" novalidate>
            <div class="fitem">
                <textarea name="ERRORS" id="ERRORS" readonly="readonly"></textarea>
            </div>
        </form>

    </div>
    <div id="dlg-buttons">
        <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok" onclick="javascript:$('#dlg').dialog('close')" style="width:90px">确定</a>
    </div>
</body>
<script type="text/javascript" src="js/jquery.min.js"></script>
<script type="text/javascript" src="js/easyui/jquery.easyui.min.js"></script>
<script type="text/javascript"
	src="js/easyui/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript">
	// 面板与内容之间的差值
	var theme = '${theme}', key = '${user.key}',autoFreshTime='${refresh}';
	// 页面初始化
	$(function() {
		// 绑定事件
		query();
	});
	function showErrors(id) {
		$('#dlg').dialog('open').dialog('setTitle', '失败原因');
		$('#fm').form('clear');
		var htmlobj=$.ajax({url:"setting/crawl/error.do",async:false,data : {key:key,id:id}});
		$("#ERRORS").val(htmlobj.responseText);
	}
	/**
	 * 执行查询
	 */
	function query() {
		$('#divStatWordsContent').html('');
		showLoading("divStatWordsContent");
		$('#divStatWordsContent').html('');
		$('#divStatWordsContent').append(
				'<div id="divMetric" style="width:100%;height:100%"></div>');
		var divMetric = $('#divMetric');
		divMetric.datagrid({
			//toolbar : '#tb',
			//title:'${title}',
			url : "setting/crawl/status.do",
			fitColumns : true,
			rownumbers : true,
			singleSelect : true,
			remoteSort : false,
			idField : "STATUS_ID",
			sortName : "UPDATE_TIME_STR",
			sortOrder : "desc",
			onLoadSuccess : function(data){
				// 继续刷新
				autoRefrech();
			},
			//pagination : pageSize < datas.length,
			pageSize : 500,
			//pageList : getPageList(pageSize),'
			columns : [ [ {
				field : 'CRAWL_NAME',
				title : '爬虫名',
				align : 'center',
				halign : 'center',
				sortable : "true"
			}, {
				field : 'MATCHINE',
				title : '所在机器',
				align : 'center',
				halign : 'center',
				sortable : "true"
			}, {
				field : 'START_TIME_STR',
				title : '开始时间',
				align : 'center',
				halign : 'center',
				sortable : "true"
			}, {
				field : 'UPDATE_TIME_STR',
				title : '最后更新时间',
				align : 'center',
				halign : 'center',
				sortable : "true"
			}, {
				field : 'STATUS',
				title : '当前状态',
				align : 'center',
				halign : 'center',
				sortable : "true",
				formatter : function(value, row) {
					if (9 == value) {
						return "未启动";
					} else if (0 == value) {
						return "正在运行";
					} else if (1 == value) {
						return "已完成";
					} else if (2 == value) {
						return '<a href="#" onclick="javascript:showErrors(' +row.STATUS_ID+ ')">失败</a>';
					} else {
						return "异常";
					}
				}
			}, {
				field : 'MSG',
				title : '最新状态信息',
				align : 'left',
				halign : 'center'

			} ] ]
		}).datagrid('clientPaging');
	}
	
	// 自定义刷新
	function refrech(){
		var divMetric = $('#divMetric');
		if (divMetric[0]){
			doClearTimeout();
			divMetric.datagrid('reload');
		}
	}
</script>
</html>
