<%@page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>页面设置</title>
<link rel="stylesheet" type="text/css"
    href='css/easyui/themes/${theme}/easyui.css'>
<link rel="stylesheet" type="text/css" href="css/easyui/themes/icon.css">
<link rel="stylesheet" type="text/css" href="css/main.css">
<style type="text/css">
#fm, #fmDetail {
    margin: 0;
    padding: 10px 10px;
}

.ftitle {
    font-size: 14px;
    font-weight: bold;
    padding: 5px 0;
    margin-bottom: 10px;
    border-bottom: 1px solid #ccc;
}

.fitem {
    margin-bottom: 5px;
}

.fitem label {
    display: inline-block;
    width: 400px;
}

.fitem input {
    width: 400px;
}
</style>
</head>
<body class="easyui-layout">
    <div id="divStatWordsContent" data-options="region:'center',split:true">
    </div>
    <div id="dlg" class="easyui-dialog" data-options="modal:true"
        style="width: 460px; height: 450px; padding: 3px 10px" closed="true"
        buttons="#dlg-buttons">
        <form id="fm" method="post" novalidate>
            <div class="fitem">
                <label>页面编号：</label> <input name="PAGE_ID" class="easyui-textbox" data-options="disabled:'true'"
                    required="required">
            </div>
            <div class="fitem">
                <label>页面名：</label> <input name="PAGE_NAME" class="easyui-textbox"
                    required="required">
            </div>
            <div class="fitem">
                <label>页面URL：</label> <input name="CONTENT_URL" class="easyui-textbox">
            </div>
            <div class="fitem">
                <label>页面参数：</label> <input name="PARAMS" class="easyui-textbox">
            </div>
            <div class="fitem">
                <label>样式：</label> <input name="ICON_CLASS" class="easyui-textbox ">
            </div>
            <div class="fitem">
                <label>宽：</label> <input name="WIDTH" type="text" class="easyui-numberbox" value="1200" data-options="min:0,precision:0">
            </div>
            <div class="fitem">
                <label>高：</label> <input name="HEIGHT" type="text" class="easyui-numberbox" value="500" data-options="min:0,precision:0">
            </div>
            <div class="fitem">
                <label>自动刷新时间（秒）：</label><input name="AUTO_FRESH_TIME" type="text" class="easyui-numberbox" value="0" data-options="min:0,precision:0"></input>
            </div>
        </form>
    </div>
    <div id="dlg-buttons">
        <a href="javascript:void(0)" class="easyui-linkbutton c6"
            iconCls="icon-ok" onclick="save()" style="width: 90px">保存</a> <a
            href="javascript:void(0)" class="easyui-linkbutton"
            iconCls="icon-cancel" onclick="javascript:$('#dlg').dialog('close')"
            style="width: 90px">取消</a>
    </div>
    <div id="tb" style="height: auto">
        <a href="javascript:void(0)" class="easyui-linkbutton"
            data-options="iconCls:'icon-add',plain:true" onclick="add()">新增</a> <a
            href="javascript:void(0)" class="easyui-linkbutton"
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
    var theme = '${theme}', key = '${user.key}',ID_KEY='PAGE_ID',URL_PREFIX='sys/page',DETAIL_URL_PREFIX='sys/page',NAME='页面',DETAIL_NAME='页面';
    // 页面初始化
    $(function() {
        query();
    });

    function initComboboxValue(){
    	
    }
    var url,SEL_DATA;
    function add() {
    	var row = $('#divMetric').datagrid('getSelected');
    	initComboboxValue();
        $('#dlg').dialog('open').dialog('setTitle', '新增' + NAME);
        $('#fm').form('clear');
        if (row) {
        	$("#PARENT_ID").combobox("setValue",row[ID_KEY]);
        }
        url = URL_PREFIX + '/add.do?key=' + key;
    }
    function edit() {
    	initComboboxValue();
        var row = $('#divMetric').datagrid('getSelected');
        if (row) {
            $('#dlg').dialog('open').dialog('setTitle', '修改' + NAME);
            $('#fm').form('load', row);
            url =URL_PREFIX + '/save.do?key=' + key + '&id=' + row[ID_KEY];
            //selectCombobox(row);
        }
    }
    function save() {
        $('#fm').form('submit', {
            url : url,
            onSubmit : function() {
                return $(this).form('validate');
            },
            success : function(result) {
                if (result) {
					showErrorMessage('操作失败',result);
                } else {
                    $('#dlg').dialog('close'); // close the dialog
                    //$('#divMetric').datagrid("reload"); // reload the user data
                    doQuery();
                }
            }
        });
    }
    function removeit() {
        var row = $('#divMetric').datagrid('getSelected');
        if (row) {
            $.messager.confirm('删除确认', '确定要删除吗？', function(r) {
                if (r) {
                    $.post(URL_PREFIX + '/delete.do', {
                        id : row[ID_KEY],
                        key : key
                    }, function(result) {
                        if (!result) {
                            //$('#divMetric').datagrid('reload'); // reload the user data 
                            $('#divMetric').datagrid('unselectAll');
                            doQuery();
                        } else {
							showErrorMessage('操作失败',result);
                        }
                    }, 'html');
                }
            });
        }
    }

    function loadData(data){
    	if (!isArray(data)){
    		return;
    	}
    	var objs = {},datas=[];
    	for (var i = 0; i < data.length; i++) {
    		var o = data[i];
    		if (o.PARENT_ID == -1){
    			o.PARENT_ID = '*';
    		}
    		if (!o.PAGE_ID){
    			o.PAGE_ID = '*';
    		}
    		if (!objs[o.PARENT_ID]){
    			objs[o.MENU_ID] = o;
    			objs[o.MENU_ID].children = [];
    		}else{
    			objs[o.PARENT_ID].children.push(o);
    		}
		}
    	for(var id in objs){
    		datas.push(objs[id]);
    	}
    	datas = datas.sort(function(a,b){
    		return a.ORDER_NUM - b.ORDER_NUM;
    	});
    	MENUS=data;
    	$('#divMetric').datagrid("loadData",data);
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
        var divMetric = $('#divMetric'), pageSize = 100;
        divMetric.datagrid({
            toolbar : '#tb',
            title : NAME + '列表',
            //url : URL_PREFIX + '/select.do',
            data:[],
            fitColumns : true,
            rownumbers : true,
            singleSelect : true,
            remoteSort : false,
            idField : ID_KEY,
            treeField:'PAGE_NAME',
            //sortName : "status",
            //sortOrder : "asc",
            pagination : false,
            pageSize : pageSize,
            pageList : getPageList(pageSize),
            onSelect:function(index,row){
            	SEL_DATA = row;
            },
            columns : [ [ {
                field : 'PAGE_ID',
                title : '页面编号',
                align : 'left',
                halign : 'center'
            },{
                field : 'PAGE_NAME',
                title : '页面名',
                align : 'left',
                halign : 'center'
            }, {
                field : 'CONTENT_URL',
                title : '页面URL',
                align : 'left',
                halign : 'center'
            }, {
                field : 'PARAMS',
                title : '页面参数',
                align : 'center',
                halign : 'center'
            },{
                field : 'ICON_CLASS',
                title : '样式',
                align : 'left',
                halign : 'center',
                sortable : "true"
            }, {
                field : 'WIDTH',
                title : '宽',
                align : 'center',
                halign : 'center'
            },{
                field : 'HEIGHT',
                title : '高',
                align : 'center',
                halign : 'center'
            },{
                field : 'AUTO_FRESH_TIME',
                title : '自动刷新时间（秒）',
                align : 'center',
                halign : 'center'
            }, {
                field : 'IN_TIME',
                title : '创建时间',
                align : 'center',
                halign : 'center'
            } , {
                field : 'UPDATE_TIME',
                title : '更新时间',
                align : 'center',
                halign : 'center'
            } ] ]
        });
        doQuery();
    }
    function doQuery(){
    	$.ajax({
        	url : URL_PREFIX + '/select.do',
			dataType : 'json',
			async : true,
			//data : params,
			success : loadData
		});
    }

</script>
</html>
