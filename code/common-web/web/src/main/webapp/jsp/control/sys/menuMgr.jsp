<%@page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>菜单设置</title>
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
    width: 100px;
}

.fitem input {
    width: 180px;
}
</style>
</head>
<body class="easyui-layout">
    <div id="divStatWordsContent" data-options="region:'center',split:true">
    </div>
    <div id="dlg" class="easyui-dialog" data-options="modal:true"
        style="width: 440px; height: 350px; padding: 10px 10px" closed="true"
        buttons="#dlg-buttons">
        <!-- <div class="ftitle">智能词条</div> -->
        <form id="fm" method="post" novalidate>
            <div class="fitem">
                <label>菜单名：</label> <input name="MENU_NAME" class="easyui-textbox"
                    required="required">
            </div>
            <div class="fitem">
                <label>上级菜单：</label> <input id="PARENT_ID" name="PARENT_ID" class="easyui-combobox"
                    required="required">
            </div>
            <div class="fitem">
                <label>指定页面：</label> <input id="PAGE_ID" name="PAGE_ID" class="easyui-combobox"
                    required="required">
            </div>
            <div class="fitem">
                <label>样式：</label> <input name="ICON_CLASS" class="easyui-textbox ">
            </div>
            <div class="fitem">
                <label>排序：</label> <input name="ORDER_NUM" type="text" class="easyui-numberbox" data-options="min:0,precision:0,value:0">
            </div>
            <div class="fitem">
                <label>是否首页显示：</label> <input name="IS_SHOW" id="IS_SHOW" class="easyui-combobox">
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
    var theme = '${theme}', key = '${user.key}',ID_KEY='MENU_ID',URL_PREFIX='sys/menu',DETAIL_URL_PREFIX='sys/page',NAME='菜单',DETAIL_NAME='页面';
    // 页面初始化
    $(function() {
        query();
        $.ajax({
        	url : DETAIL_URL_PREFIX + '/select.do',
			dataType : 'json',
			async : true,
			//data : params,
			success : function(data){
				PAGES=data;
			}
		});
    });

    function initComboboxValue(){
    	if (MENUS){
        	initComboboxWithData("PARENT_ID",MENUS,undefined,false,"MENU_ID","MENU_NAME");
        }
    	if (PAGES){
        	initComboboxWithData("PAGE_ID",PAGES,undefined,false,"PAGE_ID","PAGE_NAME");
        }
    	var showData = [{id:0,text:'否'},{id:1,text:'是'}];
        initComboboxWithData("IS_SHOW",showData,undefined,true,"id","text");
    }
    var url,SEL_DATA,MENUS,PAGES;
    function add() {
    	var row = $('#divMetric').treegrid('getSelected');
    	$('#dlg').dialog('open').dialog('setTitle', '新增' + NAME);
        $('#fm').form('clear');
        initComboboxValue();
        if (row) {
        	$("#PARENT_ID").combobox("setValue",row[ID_KEY]);
        }
        url = URL_PREFIX + '/add.do?key=' + key;
    }
    function edit() {
    	initComboboxValue();
        var row = $('#divMetric').treegrid('getSelected');
        if (row) {
            $('#dlg').dialog('open').dialog('setTitle', '修改' + NAME);
            $('#fm').form('clear').form('load', row);
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
					showErrorMessage("保存失败",result);
                } else {
                    $('#dlg').dialog('close'); // close the dialog
                    //$('#divMetric').treegrid("reload"); // reload the user data
                    doQuery();
                }
            }
        });
    }
    function removeit() {
        var row = $('#divMetric').treegrid('getSelected');
        if (row) {
            $.messager.confirm('删除确认', '确定要删除吗？', function(r) {
                if (r) {
                    $.post(URL_PREFIX + '/delete.do', {
                        id : row[ID_KEY],
                        key : key
                    }, function(result) {
                        if (!result) {
                            //$('#divMetric').treegrid('reload'); // reload the user data 
                            $('#divMetric').treegrid('unselectAll');
                            doQuery();
                        } else {
                        	showErrorMessage("删除失败",result);
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
    	$('#divMetric').treegrid("loadData",datas);
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
        divMetric.treegrid({
            toolbar : '#tb',
            title : NAME + '列表',
            //url : URL_PREFIX + '/select.do',
            data:[],
            fitColumns : true,
            rownumbers : false,
            singleSelect : true,
            remoteSort : false,
            idField : ID_KEY,
            treeField:'MENU_NAME',
            //sortName : "status",
            //sortOrder : "asc",
            pagination : false,
            pageSize : pageSize,
            pageList : getPageList(pageSize),
            onSelect:function(index,row){
            	SEL_DATA = row;
            },
            columns : [ [ {
                field : 'MENU_NAME',
                title : '菜单名',
                align : 'left',
                halign : 'center'
            }, {
                field : 'PAGE_NAME',
                title : '页面名称',
                align : 'left',
                halign : 'center'
            }, {
                field : 'ICON_CLASS',
                title : '样式',
                align : 'left',
                halign : 'center',
                sortable : "true"
            }, {
                field : 'ORDER_NUM',
                title : '序号',
                align : 'center',
                halign : 'center'
            }, {
                field : 'IS_SHOW',
                title : '是否首页显示',
                align : 'center',
                halign : 'center',
                formatter:function(value){
                	if (value == 0){
                		return '否';
                	}else{
                		return '是';
                	}
                }
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
