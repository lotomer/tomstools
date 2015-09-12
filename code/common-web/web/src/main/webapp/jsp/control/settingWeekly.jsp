<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>月周报管理</title>
<link rel="stylesheet" type="text/css"
	href='css/easyui/themes/${theme}/easyui.css'>
<link rel="stylesheet" type="text/css" href="css/easyui/themes/icon.css">
<!-- <link href="bootstrap/css/bootstrap.css" type="text/css" rel="stylesheet" /> -->
<link rel="stylesheet" type="text/css" href="css/main.css">
</head>
<body class="easyui-layout">
	<div data-options="region:'north',split:true"
		style="height: 80px; padding: 5px 30px;">
		<table>
			<tr>
				<td><label>年：</label></td>
				<td><input id="selYear" data-options='editable: false'
					class="easyui-combobox"></input></td>
				<td><label>月：</label></td>
				<td><input id="selMonth" data-options='editable: false'
					class="easyui-combobox"></input></td>
				<td><label>周：</label></td>
				<td><input id="selWeek" data-options='editable: false'
					class="easyui-combobox"></input></td>
				<td><a href="#" id="btnQuery" class="easyui-linkbutton"
					data-options="iconCls:'icon-search'" style="width: 80px">查询</a></td>
				<td><input id="fileupload" type="file" name="file" style="width:100%"></td>
			</tr>
			<tr>
				<td colspan="8"><div id="progressbar" style="width:100%;"></div></td>
			</tr>
		</table>
	</div>

	<div id="divStatWordsContent" data-options="region:'center',split:true">
	</div>
</body>
<script type="text/javascript" src="js/jquery.min.js"></script>
<script type="text/javascript" src="js/easyui/jquery.easyui.min.js"></script>
<script type="text/javascript"
	src="js/easyui/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript" src="js/vendor/jquery.ui.widget.js"></script>
<script type="text/javascript" src="js/jquery.iframe-transport.js"></script>
<script type="text/javascript" src="js/jquery.fileupload.js"></script>
<script type="text/javascript" src="js/utils.js"></script>

<script type="text/javascript">
	// 面板与内容之间的差值
	var theme = '${theme}', key = '${user.key}';
	// 页面初始化
	$(function() {
		// 绑定事件
		$('#btnQuery').bind('click', query);
		var years = [],now = new Date(),thisYear = now.getFullYear(),thisMonth = now.getMonth() + 1,thisWeek = Math.ceil(now.getDate() / 7);
		for(var year = 2015; year <= thisYear; year++){
			years.push(year);
		}
		initComboboxWithData("selYear",years,undefined,true);
		initComboboxWithData("selMonth",[{id:1,name:'一月'},{id:2,name:'二月'},{id:3,name:'三月'},{id:4,name:'四月'},{id:5,name:'五月'},{id:6,name:'六月'},{id:7,name:'七月'},{id:8,name:'八月'},{id:9,name:'九月'},{id:10,name:'十月'},{id:11,name:'十一月'},{id:12,name:'十二月'}],undefined,true);
		initComboboxWithData("selWeek",[{id:1,name:'第一周'},{id:2,name:'第二周'},{id:3,name:'第三周'},{id:4,name:'第四周'},{id:5,name:'第五周'}],undefined,true);
		$('#selYear').combobox("setValue",thisYear);
		$('#selMonth').combobox("setValue",thisMonth);
		$('#selWeek').combobox("setValue",thisWeek);
		query();
		$('#fileupload').fileupload({
	        dataType: 'html',
	        autoUpload:true,
	        url:'crawl/weekly/upload.do?key='+key + '&YEAR=' + $('#selYear').combobox("getValue") + '&MONTH=' +$('#selMonth').combobox("getValue")+ '&WEEK=' + $('#selWeek').combobox("getValue"),
	        done: function (e, data) {
	            if (data.result){
					showErrorMessage('上传失败',result);
	            }else{
	            	$.messager.alert('提示','上传完成！','info');
	            }
	            query();
	        },
	        fail:function(e,data){
	        	$.messager.alert('警告','上传失败！' + data.result, 'warning');
	        },
	        progressall: function (e, data) {
	            var progress = parseInt(data.loaded / data.total * 100, 10);
	            $('#progressbar').progressbar({
	                value: progress
	            });
	        }
	    });
		$('#fileupload').fileupload('option', {
            maxFileSize: 5000000,
            acceptFileTypes: /(\.|\/)(pdf|doc|docx)$/i
        });
	});
	function removeit(id) {
        if (id){
            $.messager.confirm('删除确认','确定要删除吗？',function(r){
                if (r){
                    $.post("crawl/weekly/delete.do",{id:id,key:key},function(result){
                        if (!result){
                            $('#divMetric').datagrid('reload');    // reload the user data
                        } else {
							showErrorMessage('操作失败',result);
                        }
                    },'html');
                }
            });
        }
    }
	/**
	 * 执行查询
	 */
	function query() {
		// 获取查询条件
		var selYear = $('#selYear').combobox("getValue"), selMonth = $(
				'#selMonth').combobox("getValue"), selWeek = $('#selWeek')
				.combobox("getValue");
		$('#divStatWordsContent').html('');
		showLoading("divStatWordsContent");
		var params = {
			key : key
		};
		if (selYear != undefined && selYear != "*") {
			params["YEAR"] = selYear;
		}
		if (selMonth != undefined && selMonth != "*") {
			params["MONTH"] = selMonth;
		}
		if (selWeek != undefined && selWeek != "*") {
			params["WEEK"] = selWeek;
		}

		$('#divStatWordsContent').html('');
		$('#divStatWordsContent').append(
				'<div id="divMetric" style="width:100%;height:100%"></div>');
		var divMetric = $('#divMetric');
		var pageSize = 100;
		divMetric.datagrid(
						{
							//title:'${title}',
							fitColumns : true,
							rownumbers : true,
							singleSelect : true,
							remoteSort : false,
							url:"crawl/query/weeklyQuery.do",
							queryParams: params,
							idField : "ID",
							//sortName : "status",
							//sortOrder : "asc",
							//pagination : pageSize < datas.length,
							pageSize : pageSize,
							//pageList : getPageList(pageSize),
							columns : [ [
									{
										field : 'YEAR',
										title : '年',
										align : 'center',
										halign : 'center'
									},
									{
										field : 'MONTH',
										title : '月',
										align : 'center',
										halign : 'center'
									},
									{
										field : 'WEEK',
										title : '周',
										align : 'center',
										halign : 'center'
									},
									{
										field : 'FILE_NAME',
										title : '文件名',
										align : 'left',
										halign : 'center',
										sortable : "true",
										formatter : function(value, row, index) {
											return '<a href="crawl/weekly/download.do?key=' + key + '&id=' +row.ID+ '">'
													+ value + '</a>';
										}
									}, {
										field : 'FILE_SIZE',
										title : '文件大小',
										align : 'center',
										halign : 'center',
										formatter:function(value){var o= storageSpaceAdapt(value, '');return o.value + o.unit;}
									}, {
										field : 'IN_TIME',
										title : '上传时间',
										align : 'center',
										halign : 'center',
										sortable : "true"
									}, {
										field : 'NICK_NAME',
										title : '上传者',
										align : 'center',
										halign : 'center',
										sortable : "true"
									}, {
										field : 'ID',
										title : '操作',
										align : 'center',
										halign : 'center',
										formatter : function(value, row, index) {
											return '<a href="#" onclick="javascript:removeit(' + value + ')">删除</a>';
										}
									} ] ]
						}).datagrid('clientPaging');
	}
	
</script>
</html>
