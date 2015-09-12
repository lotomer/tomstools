<%@page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>用户管理</title>
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
    width: 220px;
}
</style>
</head>
<body class="easyui-layout">
    <div id="divStatWordsContent" data-options="region:'center',split:true">
    </div>
    <div id="dlg" class="easyui-dialog" data-options="modal:true"
        style="width: 400px; height: 280px; padding: 10px 10px" closed="true"
        buttons="#dlg-buttons">
        <form id="fm" method="post" novalidate>
            <div class="fitem">
                <label>用户名：</label> <input name="USER_NAME" class="easyui-textbox"
                    required="required">
            </div>
            <div class="fitem">
                <label>昵称：</label> <input name="NICK_NAME" class="easyui-textbox "
                    required="required">
            </div>
            <div class="fitem">
                <label>电子邮件：</label> <input name="EMAIL" class="easyui-textbox ">
            </div>
            <div class="fitem">
                <label>手机号码：</label> <input name="PHONE_NUMBER" type="text" class="easyui-numberbox" data-options="min:0,precision:0">
            </div>
            <div class="fitem">
                <label>限制IP：</label> <input name="CLIENT_IP" class="easyui-textbox">
            </div>
            <div class="fitem">
                <span>多个IP之间用“,”连接。如：192.168.1.3,192.168.1.4</span>
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
    var theme = '${theme}', key = '${user.key}',URL_PREFIX='sys/user';
    // 页面初始化
    $(function() {
        // 绑定事件
        query();
        
        //selectCombobox();
    });
    var url;
    function add() {
        $('#dlg').dialog('open').dialog('setTitle', '新增用户');
        $('#fm').form('clear');
        url = URL_PREFIX + '/add.do?key=' + key;
    }
    function edit() {
        var row = $('#divMetric').datagrid('getSelected');
        if (row) {
            $('#dlg').dialog('open').dialog('setTitle', '修改用户');
            $('#fm').form('load', row);
            url = URL_PREFIX + '/save.do?key=' + key + '&id=' + row.USER_ID;
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
                    $('#divMetric').datagrid("reload"); // reload the user data
                }
            }
        });
    }
    function removeit() {
        var row = $('#divMetric').datagrid('getSelected');
        if (row) {
            $.messager.confirm('删除确认', '确定要删除吗？', function(r) {
                if (r) {
                    $.post(URL_PREFIX + "/delete.do", {
                        id : row.SITE_ID,
                        key : key
                    }, function(result) {
                        if (!result) {
                            $('#divMetric').datagrid('reload'); // reload the user data 
                            $('#divMetric').datagrid('unselectAll');
                        } else {
							showErrorMessage('操作失败',result);
                        }
                    }, 'html');
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
        $('#divStatWordsContent').html('');
        $('#divStatWordsContent').append(
                '<div id="divMetric" style="width:100%;height:100%"></div>');
        var divMetric = $('#divMetric'), pageSize = 15;
        divMetric.datagrid({
            toolbar : '#tb',
            title : '用户列表',
            url : URL_PREFIX + "/list.do",
            queryParams : {key:key},
            fitColumns : true,
            rownumbers : true,
            singleSelect : true,
            remoteSort : false,
            idField : "USER_ID",
            //sortName : "status",
            //sortOrder : "asc",
            pagination : true,
            pageSize : pageSize,
            pageList : getPageList(pageSize),
            columns : [ [{
                field : 'USER_NAME',
                title : '用户名',
                align : 'center',
                halign : 'center'
            },{
                field : 'NICK_NAME',
                title : '昵称',
                align : 'center',
                halign : 'center'
            },{
                field : 'EMAIL',
                title : '电子邮箱',
                align : 'center',
                halign : 'center'
            },{
                field : 'PHONE_NUMBER',
                title : '电话号码',
                align : 'center',
                halign : 'center'
            },{
                field : 'CREATE_TIME',
                title : '创建时间',
                align : 'center',
                halign : 'center'
            },{
                field : 'UPDATE_TIME',
                title : '更新时间',
                align : 'center',
                halign : 'center'
            },{
                field : 'CLIENT_IP',
                title : 'IP限制',
                align : 'center',
                halign : 'center'
            }] ]
        }).datagrid('clientPaging');
    }

    var urlDetail;
    function addDetail() {
        if (siteData == undefined)
            return;
        $('#dlgDetail').dialog('open').dialog('setTitle', '新增需要爬取的URL');
        $('#fmDetail').form('clear').form("load", siteData);
        urlDetail = 'setting/siteDetail/add.do?key=' + key;
    }
    function editDetail() {
        if (siteData == undefined)
            return;
        var row = $('#divMetricDetail').datagrid('getSelected');
        if (row) {
            $('#dlgDetail').dialog('open').dialog('setTitle', '修改需要爬取的URL');
            $('#fmDetail').form("clear").form("load", siteData).form('load',
                    row);
            urlDetail = 'setting/siteDetail/save.do?key=' + key + '&id='
                    + row.ID;
            selectCombobox(row);
        }
    }
    function saveDetail() {
        if (siteData == undefined)
            return;
        $('#fmDetail').form('submit', {
            url : urlDetail,
            onSubmit : function() {
                return $(this).form('validate');
            },
            success : function(result) {
                if (result) {
					showErrorMessage('操作失败',result);
                } else {
                    $('#dlgDetail').dialog('close'); // close the dialog
                    $('#divMetricDetail').datagrid("reload"); // reload the user data
                }
            }
        });
    }
    function removeitDetail() {
        if (siteData == undefined)
            return;
        var row = $('#divMetricDetail').datagrid('getSelected');
        if (row) {
            $.messager.confirm('删除确认', '确定要删除吗？', function(r) {
                if (r) {
                    $.post("setting/siteDetail/delete.do", {
                        id : row.ID,
                        key : key
                    }, function(result) {
                        if (!result) {
                            $('#divMetricDetail').datagrid('reload'); // reload the user data 
                            $('#divMetricDetail').datagrid('unselectAll');
                        } else {
							showErrorMessage('操作失败',result);
                        }
                    }, 'html');
                }
            });
        }
    }
</script>
</html>
