<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>智能词条设置</title>
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
            width:90px;
        }
        .fitem input{
            width:400px;
        }
        .left{
        	float:left;
        	width:530px;
        }
        .right{
        	float:left;
        	width:230px;
        }
    </style>
</head>
<body class="easyui-layout">
	<div id="divStatWordsContent" data-options="region:'center',split:true">
	</div>
	<div id="dlg" class="easyui-dialog" style="width:800px;height:500px;padding:10px 10px"
            closed="true" buttons="#dlg-buttons">
        <!-- <div class="ftitle">智能词条</div> -->
        <div class="left">
        <form id="fm" method="post" novalidate>
            <div class="fitem">
                <label>词条名：</label>
                <input name="TYPE_NAME" class="easyui-textbox" required="required">
            </div>
            <div class="fitem">
                <label>正面模板：</label>
                <input name="TEMPLATE_ZM" class="easyui-textbox " style="height:80px" data-options="multiline:true" required="required">
            </div>
            <div class="fitem">
                <label>负面模板：</label>
                <input name="TEMPLATE_FM" class="easyui-textbox " style="height:80px" data-options="multiline:true">
            </div>
            <div class="fitem">
                <label>正面模板<br>（英文）：</label>
                <input name="TEMPLATE_ZM_E" class="easyui-textbox " style="height:80px" data-options="multiline:true">
            </div>
            <div class="fitem">
                <label>负面模板<br>（英文）：</label>
                <input name="TEMPLATE_FM_E" class="easyui-textbox" style="height:80px" data-options="multiline:true">
            </div>
        </form>
        </div>
        <div class="right">
	        <p>模板说明：</p>
	        <ul>
	        	<li>词汇之间支持运算符：AND、OR、NOT</li>
	        	<li>支持用圆括号“()”构建子表达式</li>
	        	<li>“~”表示模糊检索，如检索拼写类似于”roam”的项这样写：roam~将找到形如foam和roams的单词；roam~0.8，检索返回相似度在0.8以上的记录。</li>
	        	<li>邻近检索，如检索相隔10个单词的”apache”和”jakarta”，”jakarta apache”~10</li>
	        	<li>“^”控制相关度检索，如检索jakarta apache，同时希望去让”jakarta”的相关度更加好，那么在其后加上”^”符号和增量值，即jakarta^4 apache</li>
	        	<li>“+” 存在操作符，要求符号”+”后的项必须在文档相应的域中存在</li>
	        </ul>
        </div>
    </div>
    <div id="dlg-buttons">
        <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok" onclick="save()" style="width:90px">保存</a>
        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:$('#dlg').dialog('close')" style="width:90px">取消</a>
    </div>
	<div id="tb" style="height: auto">
		<a href="javascript:void(0)" class="easyui-linkbutton"
			data-options="iconCls:'icon-add',plain:true" onclick="add()">新增</a>
		<a href="javascript:void(0)" class="easyui-linkbutton"
			data-options="iconCls:'icon-remove',plain:true" onclick="removeit()">删除</a>
		<a href="javascript:void(0)" class="easyui-linkbutton"
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
             url = 'setting/words/save.do?key='+ key + '&id='+row.TYPE_ID;
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
                     $.messager.show({
                         title: 'Error',
                         msg: result
                     });
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
                     $.post("setting/words/delete.do",{id:row.TYPE_ID,key:key},function(result){
                         if (!result){
                             $('#divMetric').datagrid('reload');    // reload the user data
                             $('#divMetric').datagrid('unselectAll');
                         } else {
                             $.messager.show({    // show error message
                                 title: '异常',
                                 msg: result
                             });
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
		var divMetric = $('#divMetric'), pageSize = 15;
		divMetric.datagrid({
			toolbar : '#tb',
			//title:'${title}',
			url:"setting/words/select.do",
			fitColumns : true,
			rownumbers : true,
			singleSelect : true,
			remoteSort : false,
			idField : "TYPE_ID",
			//sortName : "status",
			//sortOrder : "asc",
			pagination : true,
			pageSize : pageSize,
			pageList : getPageList(pageSize),
			columns : [ [ {
				field : 'TYPE_NAME',
				title : '词条名',
				align : 'center',
				halign : 'center',
				sortable : "true"
			}, {
				field : 'TEMPLATE_ZM',
				title : '正面模板',
				align : 'center',
				halign : 'center'
			}, {
				field : 'TEMPLATE_FM',
				title : '负面模板',
				align : 'center',
				halign : 'center'
			}, {
				field : 'TEMPLATE_ZM_E',
				title : '正面模板（英文）',
				align : 'center',
				halign : 'center'
			}, {
				field : 'TEMPLATE_FM_E',
				title : '负面模板（英文）',
				align : 'center',
				halign : 'center'
			}] ]
		}).datagrid('clientPaging');
	}
</script>
</html>
