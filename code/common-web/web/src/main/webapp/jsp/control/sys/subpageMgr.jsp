<%@page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>子页面</title>
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
    width: 80px;
}

.fitem input {
    width: 180px;
}
</style>
</head>
<body class="easyui-layout">
    <div id="divContent" data-options="region:'west',split:true"
        style="width: 240px;"></div>
    <div id="divContentDetail" data-options="region:'center',split:true">
    </div>
    <div id="dlg" class="easyui-dialog" data-options="modal:true"
        style="width: 340px; height: 150px; padding: 10px 10px" closed="true"
        buttons="#dlg-buttons">
        <form id="fm" method="post" novalidate>
            <div class="fitem">
                <label>主页面</label> <input id="PAGE_ID" name="PAGE_ID" class="easyui-combobox"
                    required="required"></input>
            </div>
        </form>
    </div>
    <div id="dlg-buttons">
        <a href="javascript:void(0)" class="easyui-linkbutton c6"
            iconCls="icon-ok" onclick="save()" style="width: 90px">确定</a> <a
            href="javascript:void(0)" class="easyui-linkbutton"
            iconCls="icon-cancel" onclick="javascript:$('#dlg').dialog('close')"
            style="width: 90px">取消</a>
    </div>
    <div id="tb" style="height: auto">
        <a href="javascript:void(0)" class="easyui-linkbutton"
            data-options="iconCls:'icon-add',plain:true" onclick="add()">新增</a> <a
            href="javascript:void(0)" class="easyui-linkbutton"
            data-options="iconCls:'icon-remove',plain:true" onclick="removeit()">删除</a>
        <!-- <a href="javascript:void(0)" class="easyui-linkbutton"
            data-options="iconCls:'icon-edit',plain:true" onclick="edit()">修改</a> -->
    </div>

    <div id="dlgDetail" class="easyui-dialog" data-options="modal:true"
        style="width: 340px; height: 220px; padding: 10px 10px" closed="true"
        buttons="#dlg-buttonsDetail">
        <!-- <div class="ftitle">智能词条</div> -->
        <form id="fmDetail" method="post" novalidate>
        	<input type="hidden" id="PAGE_ID" name="PAGE_ID">
            <div class="fitem">
            	<label>子页面</label> <input id="SUB_PAGE_ID" name="SUB_PAGE_ID" class="easyui-combobox"></input>
            </div>
            <div class="fitem">
                <label>宽：</label> <input name="WIDTH"  type="text" class="easyui-numberbox" data-options="min:0,precision:0,max:5000" required="required"></input>
            </div>
            <div class="fitem">
                <label>高：</label> <input name="HEIGHT" type="text" class="easyui-numberbox" data-options="min:0,precision:0,max:2000" required="required"></input>
            </div>
            <div class="fitem">
                <label>序号：</label> <input name="ORDER_NUM" type="text" class="easyui-numberbox" data-options="min:0,precision:0,max:100" required="required"></input>
            </div>
        </form>
    </div>
    <div id="dlg-buttonsDetail">
        <a href="javascript:void(0)" class="easyui-linkbutton c6"
            iconCls="icon-ok" onclick="saveDetail()" style="width: 90px">保存</a> <a
            href="javascript:void(0)" class="easyui-linkbutton"
            iconCls="icon-cancel"
            onclick="javascript:$('#dlgDetail').dialog('close')"
            style="width: 90px">取消</a>
    </div>
    <div id="tbDetail" style="height: auto">
        <a href="javascript:void(0)" class="easyui-linkbutton"
            data-options="iconCls:'icon-add',plain:true" onclick="addDetail()">新增</a>
        <a href="javascript:void(0)" class="easyui-linkbutton"
            data-options="iconCls:'icon-remove',plain:true"
            onclick="removeitDetail()">删除</a> <a href="javascript:void(0)"
            class="easyui-linkbutton"
            data-options="iconCls:'icon-edit',plain:true" onclick="editDetail()">修改</a>
    </div>
</body>
<script type="text/javascript" src="js/jquery.min.js"></script>
<script type="text/javascript" src="js/easyui/jquery.easyui.min.js"></script>
<script type="text/javascript"
    src="js/easyui/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript">
    // 面板与内容之间的差值
    var theme = '${theme}', key = '${user.key}',ID_KEY='PAGE_ID',URL_PREFIX='sys/subpage',DETAIL_URL_PREFIX='sys/subpage',NAME='主页面',DETAIL_NAME='子页面';
    // 页面初始化
    $(function() {
    	initQuery();
    	initQueryDetail();
        query();
    });
    function initMainComboboxValue(){
      	initCombobox("PAGE_ID","sys/page/select.do?key=" + key,undefined,false,"PAGE_ID","PAGE_NAME");
    }
    var url,PAGES,DATA;
    function add() {
        $('#dlg').dialog('open').dialog('setTitle', '新增' + NAME);
        //$('#fm').form('clear');
        initMainComboboxValue();
        url = URL_PREFIX + '/add.do?key=' + key;
    }
    function edit() {
    	initMainComboboxValue();
        var row = $('#divMetric').datagrid('getSelected');
        if (row) {
            $('#dlg').dialog('open').dialog('setTitle', '修改' + NAME);
            $('#fm').form('load', row);
            url =URL_PREFIX + '/save.do?key=' + key + '&id=' + row[ID_KEY];
        }
    }
    function save() {
    	var exists = false,data = $('#divMetric').datagrid("getData"),pageId = $('#PAGE_ID').combobox("getValue"),pageName = $('#PAGE_ID').combobox("getText");
    	// 判断是否已经存在
    	for(var i = 0,iLen = data.rows.length; i<iLen;i++){
    		if (pageId == data.rows[i].PAGE_ID){
    			exists = true;
    			break;
    		}
    	}
    	if (!exists){
	    	data.total += 1;
	    	data.rows.push({PAGE_ID:pageId,PAGE_NAME:pageName});
	    	$('#divMetric').datagrid("loadData",data);
	    	$('#divMetric').datagrid("unselectAll");
	    	$('#divMetricDetail').datagrid("loadData",[]);
    	}
    	$('#dlg').dialog('close');
       /* $('#fm').form('submit', {
            url : url,
            onSubmit : function() {
                return $(this).form('validate');
            },
            success : function(result) {
                if (result) {
					showErrorMessage('操作失败',result);
                } else {
                    $('#dlg').dialog('close'); // close the dialog
                    $('#divMetric').datagrid("reload"); // reload the user data
                }
            }
        });*/
    }
    function removeit() {
        var row = $('#divMetric').datagrid('getSelected');
        if (row) {
            $.messager.confirm('删除确认', '确定要删除吗？', function(r) {
                if (r) {
                    $.post(URL_PREFIX + '/deleteAll.do', {
                        id : row[ID_KEY],
                        key : key
                    }, function(result) {
                        if (!result) {
                            $('#divMetric').datagrid('reload'); // reload the user data 
                            $('#divMetric').datagrid('unselectAll');
                        } else {
                        	showErrorMessage('删除失败',result);
                        }
                    }, 'html');
                }
            });
        }
    }

    function successCallback(datas){
    	var pages = {};
    	for (var i = 0; i < datas.length; i++) {
    		var data = datas[i];
			if (!pages[data.PAGE_ID]){
				pages[data.PAGE_ID] = {PAGE_ID:data.PAGE_ID,PAGE_NAME:data.PAGE_NAME,children:[]};
			}
			
			pages[data.PAGE_ID].children.push({PAGE_ID:data.PAGE_ID,SUB_PAGE_ID:data.SUB_PAGE_ID,SUB_PAGE_NAME:data.SUB_PAGE_NAME,ORDER_NUM:data.ORDER_NUM,WIDTH:data.WIDTH,HEIGHT:data.HEIGHT});
		}
    	
    	// 显示主页面列表
    	var pageList = [];
    	for ( var pageId in pages) {
			pageList.push(pages[pageId]);
		}
    	PAGES=pages;
    	
    	$('#divMetric').datagrid("loadData",pageList);
    	$('#divMetric').datagrid("unselectAll");
    	$('#divMetricDetail').datagrid("unselectAll");
    	$('#divMetricDetail').datagrid("loadData",[]);
    	if (DATA){
        	$('#divMetric').datagrid("selectRecord",DATA.PAGE_ID);
    		//DATA = undefined;
    	}
    }
    function initQuery(){
    	$('#divContent').html('');
        showLoading("divContent");
        $('#divContent').html('');
        $('#divContent').append(
                '<div id="divMetric" style="width:100%;height:100%"></div>');
        var divMetric = $('#divMetric'), pageSize = 150;
        divMetric.datagrid({
            toolbar : '#tb',
            title : NAME + '列表',
            //url : URL_PREFIX + '/select.do',
            data:[],
            width:233,
            fitColumns : true,
            rownumbers : true,
            singleSelect : true,
            remoteSort : false,
            idField : ID_KEY,
            //sortName : "status",
            //sortOrder : "asc",
            pagination : false,
            pageSize : pageSize,
            pageList : getPageList(pageSize),
            onSelect : function(index, row) {
            	DATA = row;
                if (PAGES && PAGES[row.PAGE_ID]){
	                $('#divMetricDetail').datagrid({
	                    title : DETAIL_NAME + "列表【" + row.PAGE_NAME + "】"
	                }).datagrid("loadData", PAGES[row.PAGE_ID].children);
	                $('#divMetricDetail').datagrid("unselectAll");
                }
            },
            columns : [ [ {
                field : 'PAGE_NAME',
                title : NAME + '名',
                align : 'center',
                halign : 'center'
            }] ]
        }).datagrid('clientPaging');
    }
    /**
     * 执行查询
     */
    function query() {
        loadData("", URL_PREFIX + '/select.do', {key:key}, successCallback);
        initDetailComboboxValue();
    }
    function initDetailComboboxValue(){
      	initCombobox("SUB_PAGE_ID","sys/page/select.do?key=" + key,undefined,true,"PAGE_ID","PAGE_NAME");
    }
    var urlDetail;
    function addDetail() {
        if (DATA == undefined)
            return;
        $('#dlgDetail').dialog('open').dialog('setTitle', '新增' + DETAIL_NAME);
        $('#SUB_PAGE_ID').combobox('readonly',false);
        $('#fmDetail').form('clear');
        initDetailComboboxValue();
        $('#fmDetail').form('load', DATA);
        urlDetail = DETAIL_URL_PREFIX + '/add.do?key=' + key;
    }
    function editDetail() {
        if (DATA == undefined)
            return;
        
        var row = $('#divMetricDetail').datagrid('getSelected');
        if (row) {
        	log(row);
            $('#dlgDetail').dialog('open').dialog('setTitle', '修改' + DETAIL_NAME);
            $('#fmDetail').form("clear");
            //initDetailComboboxValue();
            $('#fmDetail').form('load',row);
            urlDetail =DETAIL_URL_PREFIX + '/save.do?key=' + key;

            $('#SUB_PAGE_ID').combobox('readonly',true);
        }
    }
    function saveDetail() {
        $('#fmDetail').form('submit', {
            url : urlDetail,
            onSubmit : function() {
                return $(this).form('validate');
            },
            success : function(result) {
                if (result) {
                	showErrorMessage("保存失败",result);
                } else {
                    $('#dlgDetail').dialog('close'); // close the dialog
                    query();
                    //$('#divMetricDetail').datagrid("reload"); // reload the user data
                }
            }
        });
    }
    function removeitDetail() {
        if (DATA == undefined)
            return;
        var row = $('#divMetricDetail').datagrid('getSelected');
        if (row) {
            $.messager.confirm('删除确认', '确定要删除吗？', function(r) {
                if (r) {
                    $.post(DETAIL_URL_PREFIX + "/delete.do", {
                        PAGE_ID : row.PAGE_ID,
                        SUB_PAGE_ID : row.SUB_PAGE_ID,
                        key : key
                    }, function(result) {
                        if (!result) {
                        	query();
                            //$('#divMetricDetail').datagrid('reload'); // reload the user data 
                            //$('#divMetricDetail').datagrid('unselectAll');
                        } else {
                        	showErrorMessage('删除失败',result);
                        }
                    }, 'html');
                }
            });
        }
    }
	
    /**
     * 执行查询
     */
    function initQueryDetail() {
    	$('#divContentDetail').html('');
        showLoading("divContentDetail");
        $('#divContentDetail').html('');
        $('#divContentDetail')
                .append(
                        '<div id="divMetricDetail" style="width:100%;height:100%"></div>');
        var divMetric = $('#divMetricDetail'), pageSize = 15, params = {
            key : key
        };

        divMetric.datagrid({
            toolbar : '#tbDetail',
            title :  DETAIL_NAME + "列表",
            //url : "setting/siteDetail/select.do",
            data:[],
            queryParams : params,
            fitColumns : true,
            rownumbers : true,
            singleSelect : true,
            remoteSort : false,
            idField : "SUB_PAGE_ID",
            sortName : "ORDER_NUM",
            sortOrder : "asc",
            pagination : true,
            pageSize : pageSize,
            pageList : getPageList(pageSize),
            columns : [ [  {
                field : 'SUB_PAGE_NAME',
                title : DETAIL_NAME + '名',
                align : 'center',
                halign : 'center'
            } ,{
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
                field : 'ORDER_NUM',
                title : '序号',
                align : 'center',
                halign : 'center'
            } ] ]
        }).datagrid('clientPaging');
    }
</script>
</html>
