<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>爬虫设置</title>
<link rel="stylesheet" type="text/css"
	href='css/easyui/themes/${theme}/easyui.css'>
<link rel="stylesheet" type="text/css" href="css/easyui/themes/icon.css">
<link rel="stylesheet" type="text/css" href="css/main.css">
<style type="text/css">
        #fm{
            margin:0;
            padding:10px 10px;
        }
        .ftitle{
            font-size:14px;
            font-weight:bold;
            padding:5px 0;
            margin-bottom:10px;
            border-bottom:1px solid #ccc;
        }
        .fitem{
            margin-bottom:5px;
        }
        .fitem label{
            display:inline-block;
            width:140px;
        }
        .fitem input{
            width:100px;
        }
    </style>
</head>
<body class="easyui-layout">
	<div id="divStatWordsContent" data-options="region:'center',split:true">
	</div>
	<div id="dlg" class="easyui-dialog" data-options="modal:true" style="width:400px;height:200px;padding:10px 10px"
            closed="true" buttons="#dlg-buttons">
        <!-- <div class="ftitle">智能词条</div> -->
        <form id="fm" method="post" novalidate>
            <div class="fitem">
                <label>爬虫名：</label>
                <input name="CRAWL_NAME" class="easyui-textbox" required="true">
            </div>
            <div class="fitem">
                <label>频率（单位：分钟）：</label>
                <input name="CRAWL_FREQUENCY" class="easyui-textbox " required="true">
            </div>
        </form>

    </div>
    <div id="dlg-buttons">
        <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok" onclick="save()" style="width:90px">保存</a>
        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:$('#dlg').dialog('close')" style="width:90px">取消</a>
    </div>
	<div id="tb" style="height: auto">
		<!-- <a href="javascript:void(0)" class="easyui-linkbutton"
			data-options="iconCls:'icon-add',plain:true" onclick="add()">新增</a>
		<a href="javascript:void(0)" class="easyui-linkbutton"
			data-options="iconCls:'icon-remove',plain:true" onclick="removeit()">删除</a>
		 --><a href="javascript:void(0)" class="easyui-linkbutton"
			data-options="iconCls:'icon-edit',plain:true" onclick="edit()">修改</a>
	</div>
</body>
<script type="text/javascript" src="js/jquery.min.js"></script>
<script type="text/javascript" src="js/easyui/jquery.easyui.min.js"></script>
<script type="text/javascript"
	src="js/easyui/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript">
	// 面板与内容之间的差值
	var theme = '${theme}', key = '${user.key}';
	// 页面初始化
	$(function() {
		// 绑定事件
		query();
	});
	var url;
    function add(){
        $('#dlg').dialog('open').dialog('setTitle','新增词条');
        $('#fm').form('clear');
        url = 'setting/words/add.do?key='+key;
    }
    function edit(){
        var row = $('#divMetric').datagrid('getSelected');
        if (row){
            $('#dlg').dialog('open').dialog('setTitle','修改词条');
            $('#fm').form('load',row);
            url = 'setting/crawl/save.do?key='+ key + '&id='+row.CRAWL_CODE;
        }
    }
    function save(){
        $('#fm').form('submit',{
            url: url,
            onSubmit: function(){
                return $(this).form('validate');
            },
            success: function(result){
                if (result){
					showErrorMessage('操作失败',result);
                } else {
                    $('#dlg').dialog('close');        // close the dialog
                    $('#divMetric').datagrid("reload");    // reload the user data
                }
            }
        });
    }
    function removeit(){
        var row = $('#divMetric').datagrid('getSelected');
        if (row){
            $.messager.confirm('删除确认','确定要删除吗？',function(r){
                if (r){
                    $.post("setting/crawl/delete.do",{id:row.CRAWL_CODE,key:key},function(result){
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
		$('#divStatWordsContent').html('');
		showLoading("divStatWordsContent");
		loadData();
	}
	function loadData() {
		$('#divStatWordsContent').html('');
		$('#divStatWordsContent').append(
				'<div id="divMetric" style="width:100%;height:100%"></div>');
		var divMetric = $('#divMetric');
		divMetric.datagrid({
			toolbar : '#tb',
			//title:'${title}',
			url : "setting/crawl/select.do",
			fitColumns : true,
			rownumbers : true,
			singleSelect : true,
			remoteSort : false,
			idField : "CRAWL_CODE",
			//sortName : "status",
			//sortOrder : "asc",
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
				field : 'CRAWL_FREQUENCY',
				title : '爬取频率（单位：分钟）',
				align : 'center',
				halign : 'center',
				sortable : "true"
			}] ]
		}).datagrid('clientPaging');
	}
</script>
</html>
