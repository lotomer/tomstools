<%@page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>角色管理</title>
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
    <div id="divMainContent" data-options="region:'west',split:true"
        style="width: 300px;"></div>
    <div id="divContentDetail" data-options="region:'center',split:true,onResize:resizeContent">
    </div>
    <div id="dlg" class="easyui-dialog" data-options="modal:true"
        style="width: 340px; height: 150px; padding: 10px 10px" closed="true"
        buttons="#dlg-buttons">
        <form id="fm" method="post" novalidate>
            <input type="hidden" name="ROLE_ID" id="ROLE_ID" />
            <div class="fitem">
                <label>角色名：</label> <input id="ROLE_NAME" name="ROLE_NAME" class="easyui-textbox"
                    required="required"></input>
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

    <div id="tbDetailUser" style="height: auto">
         <a href="javascript:void(0)" class="easyui-linkbutton"
            data-options="iconCls:'icon-save',plain:true" onclick="saveUserDetail()">保存</a>
    </div>
    <div id="tbDetailMenu" style="height: auto">
         <a href="javascript:void(0)" class="easyui-linkbutton"
            data-options="iconCls:'icon-save',plain:true" onclick="saveMenuDetail()">保存</a>
    </div>
    <div id="tbDetailPage" style="height: auto">
         <a href="javascript:void(0)" class="easyui-linkbutton"
            data-options="iconCls:'icon-save',plain:true" onclick="savePageDetail()">保存</a>
    </div>
</body>
<script type="text/javascript" src="js/jquery.min.js"></script>
<script type="text/javascript" src="js/easyui/jquery.easyui.min.js"></script>
<script type="text/javascript"
    src="js/easyui/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript">
    // 面板与内容之间的差值
    var theme = '${theme}', key = '${user.key}',URL_PREFIX='sys/role';
    // 页面初始化
    $(function() {
        getRoleUsers();
        getRoleMenus();
        getRolePages();
        query();
        //queryDetail();
    });
    function resizeContent (w,h) {
        $('#divContentDetail').tabs();
        $('#divContentDetail').tabs('resize',{width:w,height:h});
    }
    function getRoleUsers(){
     // 获取角色对应的用户列表
        $.ajax({
            url : URL_PREFIX + "/selectUsers.do?_r=" + Math.random(),
            dataType : 'json',
            async : true,
            data : {key:key},
            success : function(data) {
                if (data) {
                    var ids = {};
                    for (var i = 0, iLen = data.length; i < iLen; i++) {
                        if (!ids[data[i]["ROLE_ID"]]){
                            ids[data[i]["ROLE_ID"]] = {};
                        }
                        ids[data[i]["ROLE_ID"]][data[i]["USER_ID"]] = 1;
                    }
                    window.ROLE_USERIDS = ids;
                }
            }
        });
    }
    function getRoleMenus(){
        // 获取角色对应的菜单列表
           $.ajax({
               url : URL_PREFIX + "/selectMenus.do?_r=" + Math.random(),
               dataType : 'json',
               async : true,
               data : {key:key},
               success : function(data) {
                   if (data) {
                       var ids = {};
                       for (var i = 0, iLen = data.length; i < iLen; i++) {
                           if (!ids[data[i]["ROLE_ID"]]){
                               ids[data[i]["ROLE_ID"]] = {};
                           }
                           ids[data[i]["ROLE_ID"]][data[i]["MENU_ID"]] = 1;
                       }
                       window.ROLE_MENUIDS = ids;
                   }
               }
           });
       }
    function getRolePages(){
        // 获取角色对应的页面列表
         $.ajax({
             url : URL_PREFIX + "/selectPages.do?_r=" + Math.random(),
             dataType : 'json',
             async : true,
             data : {key:key},
             success : function(data) {
                 if (data) {
                     var ids = {};
                     for (var i = 0, iLen = data.length; i < iLen; i++) {
                         if (!ids[data[i]["ROLE_ID"]]){
                             ids[data[i]["ROLE_ID"]] = {};
                         }
                         ids[data[i]["ROLE_ID"]][data[i]["PAGE_ID"]] = 1;
                     }
                     window.ROLE_PAGEIDS = ids;
                 }
             }
         });
     }
    var url, ROLE_ID,isUserDetail;
    function add() {
        $('#dlg').dialog('open').dialog('setTitle', '新增角色');
        $('#fm').form('clear');
        url = URL_PREFIX + '/add.do?key=' + key;
    }
    function edit() {
        var row = $('#divMetric').datagrid('getSelected');
        if (row) {
            $('#dlg').dialog('open').dialog('setTitle', '修改角色');
            $('#fm').form('load', row);
            url = URL_PREFIX + '/save.do?key=' + key + '&id=' + row.ROLE_ID;
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
                        id : row.ROLE_ID,
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
        $('#divMainContent').html('');
        showLoading("divMainContent");
        $('#divMainContent').html('');
        $('#divMainContent').append(
                '<div id="divMetric" style="width:100%;height:100%"></div>');
        var divMetric = $('#divMetric'), pageSize = 100;
        divMetric.datagrid({
            toolbar : '#tb',
            title : '角色列表',
            url : URL_PREFIX + "/select.do",
            fitColumns : true,
            rownumbers : true,
            singleSelect : true,
            remoteSort : false,
            idField : "ROLE_ID",
            //sortName : "status",
            //sortOrder : "asc",
            pagination : false,
            pageSize : pageSize,
            pageList : getPageList(pageSize),
            /*onSelect : function(index, row) {
                siteData = row;
                $('#divMetricDetail').datagrid({
                    title : "角色【" + row.ROLE_NAME + "】"
                }).datagrid("load", {
                    key : key,
                    ROLE_ID : row.ROLE_ID
                });
                $('#divMetricDetail').datagrid("unselectAll");
            },*/
            columns : [ [ {
                field : 'ROLE_NAME',
                title : '角色名',
                align : 'center',
                halign : 'center'
            },{
                field : 'B',
                title : '用户授权',
                align : 'center',
                halign : 'center',
                formatter: function(value,row){
                    return '<a href="#" onclick="javascript:showUsers(' + row.ROLE_ID + ')">用户授权</a>';
                }
            },{
                field : 'A',
                title : '菜单授权',
                align : 'center',
                halign : 'center',
                formatter: function(value,row){
                    return '<a href="#" onclick="javascript:showMenus(' + row.ROLE_ID + ')">菜单授权</a>';
                }
            },{
                field : 'C',
                title : '页面授权',
                align : 'center',
                halign : 'center',
                formatter: function(value,row){
                    return '<a href="#" onclick="javascript:showPages(' + row.ROLE_ID + ')">页面授权</a>';
                }
            }] ]
        }).datagrid('clientPaging');
    }
    function showUsers(roleId){
        ROLE_ID=roleId;
        if (!window.ROLE_USERIDS){
            return;
        }
     // 判断是否已经存在
        var name='角色用户授权',id='divRoleUsersDetail',containerId='divContentDetail',contentId=id + 'Content',tab = $('#' + containerId).tabs('getTab',name);
        if(tab){
            $('#' + containerId).tabs('select',name);
        }else{
            $('#' + containerId).tabs('add',{
                id:id,
                title:name,
                height:480,
                //content:'Tab Body',
                //href : url,
                fit:true,
                closable:false
            });
        }
        $('#' + containerId).tabs("select",name);
        if (!$('#'  + contentId)[0]){
	        $('#' + id).html('');
	        showLoading( id);
	        $('#' + id).html('');
	        $('#' + id)
	                .append(
	                        '<div id="' +contentId+ '" style="width:100%;height:100%"></div>');
	        var divMetric = $('#' + contentId), pageSize = 1000, params = {
	            key : key,id:ROLE_ID
	        };
	        divMetric.datagrid({
                toolbar : '#tbDetailUser',
                //title : "角色对应的用户",
                url : "sys/user/list.do",
                queryParams : params,
                fitColumns : true,
                rownumbers : true,
                singleSelect : false,
                remoteSort : false,
                idField : "USER_ID",
                //sortName : "status",
                //sortOrder : "asc",
                pagination : false,
                pageSize : pageSize,
                pageList : getPageList(pageSize),
                columns : [[{
                    field:'ck',
                    checkbox:true
                },{
                    field : 'USER_NAME',
                    title : '用户名',
                    align : 'center',
                    halign : 'center',
                    formatter:function(value,row,index){
                        if(window.ROLE_USERIDS[ROLE_ID] && window.ROLE_USERIDS[ROLE_ID][row.USER_ID]){
                            divMetric.datagrid("selectRow",index);
                        }
                        return value;
                    }
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
        }else{
            $('#' + contentId).datagrid('unselectAll');
            $('#' + contentId).datagrid("reload",{key : key,id:ROLE_ID,url:"sys/user/list.do"});
        }
    }
    function showMenus(roleId){
        ROLE_ID=roleId;
        if (!window.ROLE_MENUIDS){
            return;
        }
        var name='角色菜单授权',id='divRoleMenusDetail',containerId='divContentDetail',contentId=id + 'Content',tab = $('#' + containerId).tabs('getTab',name);
        if(tab){
            $('#' + containerId).tabs('select',name);
        }else{
            $('#' + containerId).tabs('add',{
                id:id,
                title:name,
                height:480,
                //content:'Tab Body',
                //href : url,
                fit:true,
                closable:false
            });
        }
        $('#' + containerId).tabs("select",name);
        if (!$('#'  + contentId)[0]){
            $('#' + id).html('');
            showLoading( id);
            $('#' + id).html('');
            $('#' + id)
                    .append(
                            '<div id="' +contentId+ '" style="width:100%;height:100%"></div>');
            var divMetric = $('#' + contentId), pageSize = 1000, params = {
                key : key,id:ROLE_ID
            };
            divMetric.datagrid({
                toolbar : '#tbDetailMenu',
                //title : "角色对应的用户",
                url : "sys/menu/select.do",
                queryParams : params,
                fitColumns : true,
                rownumbers : true,
                singleSelect : false,
                remoteSort : false,
                idField : "MENU_ID",
                //sortName : "status",
                //sortOrder : "asc",
                pagination : false,
                pageSize : pageSize,
                pageList : getPageList(pageSize),
                columns : [[{
                    field:'ck',
                    checkbox:true
                },{
                    field : 'MENU_NAME',
                    title : '菜单名',
                    align : 'center',
                    halign : 'center',
                    formatter:function(value,row,index){
	                    if(window.ROLE_MENUIDS[ROLE_ID] && window.ROLE_MENUIDS[ROLE_ID][row.MENU_ID]){
	                        divMetric.datagrid("selectRow",index);
	                    }
                        return value;
                    }
                },{
                    field : 'ORDER_NUM',
                    title : '序号',
                    align : 'center',
                    halign : 'center'
                },{
                    field : 'ICON_CLASS',
                    title : '样式',
                    align : 'center',
                    halign : 'center'
                },{
                    field : 'PAGE_NAME',
                    title : '页面名',
                    align : 'center',
                    halign : 'center',
                    formatter: function(value,row){
                        return value;
                    }
                }] ]
            }).datagrid('clientPaging');
        }else{
            $('#' + contentId).datagrid('unselectAll').datagrid("reload");
        }
    }
    function showPages(roleId){
        ROLE_ID=roleId;
        if (!window.ROLE_PAGEIDS){
            return;
        }
        var name='角色页面授权',id='divRolePagesDetail',containerId='divContentDetail',contentId=id + 'Content',tab = $('#' + containerId).tabs('getTab',name);
        if(tab){
            $('#' + containerId).tabs('select',name);
        }else{
            $('#' + containerId).tabs('add',{
                id:id,
                title:name,
                height:480,
                //content:'Tab Body',
                //href : url,
                fit:true,
                closable:false
            });
        }
        $('#' + containerId).tabs("select",name);
        if (!$('#'  + contentId)[0]){
            $('#' + id).html('');
            showLoading( id);
            $('#' + id).html('');
            $('#' + id)
                    .append(
                            '<div id="' +contentId+ '" style="width:100%;height:100%"></div>');
            var divMetric = $('#' + contentId), pageSize = 1000, params = {
                key : key,id:ROLE_ID
            };
            divMetric.datagrid({
                toolbar : '#tbDetailPage',
                //title : "角色对应的用户",
                url : "sys/page/select.do",
                queryParams : params,
                fitColumns : true,
                rownumbers : true,
                singleSelect : false,
                remoteSort : false,
                idField : "PAGE_ID",
                //sortName : "status",
                //sortOrder : "asc",
                pagination : false,
                pageSize : pageSize,
                pageList : getPageList(pageSize),
                columns : [[{
                    field:'ck',
                    checkbox:true
                },{
                    field : 'PAGE_NAME',
                    title : '页面名',
                    align : 'center',
                    halign : 'center',
                    formatter:function(value,row,index){
                        if(window.ROLE_PAGEIDS[ROLE_ID] && window.ROLE_PAGEIDS[ROLE_ID][row.PAGE_ID]){
                            divMetric.datagrid("selectRow",index);
                        }
                        return value;
                    }
                },{
                    field : 'CONTENT_URL',
                    title : '页面URL',
                    align : 'left',
                    halign : 'center'
                },{
                    field : 'PARAMS',
                    title : '页面参数',
                    align : 'left',
                    halign : 'center'
                },{
                    field : 'ICON_CLASS',
                    title : '样式',
                    align : 'center',
                    halign : 'center'
                },{
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
                    title : '刷新时间（秒）',
                    align : 'center',
                    halign : 'center'
                }] ]
            }).datagrid('clientPaging');
        }else{
            $('#' + contentId).datagrid('unselectAll').datagrid("reload");
        }
    }
    function saveUserDetail() {
        var url,params={key:key,id:ROLE_ID},ID_NAME,PARAM_NAME,param_value=[],rows = $('#divRoleUsersDetailContent').datagrid("getSelections");
        // 保存角色对应的用户
        ID_NAME="USER_ID";
        PARAM_NAME="USER_IDS";
        url = "sys/role/saveUsers.do";
        for (var i = 0, iLen = rows.length; i < iLen; i++) {
            param_value.push(rows[i][ID_NAME]);
        }
        params[PARAM_NAME] = param_value.join(",");
        saveDetail(url,params,getRoleUsers);
    }
    function saveMenuDetail() {
        var url,params={key:key,id:ROLE_ID},ID_NAME,PARAM_NAME,param_value=[],rows = $('#divRoleMenusDetailContent').datagrid("getSelections");
        // 保存角色对应的菜单
        ID_NAME="MENU_ID";
        PARAM_NAME="MENU_IDS";
        url = "sys/role/saveMenus.do";
        for (var i = 0, iLen = rows.length; i < iLen; i++) {
            param_value.push(rows[i][ID_NAME]);
        }
        params[PARAM_NAME] = param_value.join(",");

        saveDetail(url,params,getRoleMenus);
    }

    function savePageDetail() {
        var url,params={key:key,id:ROLE_ID},ID_NAME,PARAM_NAME,param_value=[],rows = $('#divRolePagesDetailContent').datagrid("getSelections");
        // 保存角色对应的菜单
        ID_NAME="PAGE_ID";
        PARAM_NAME="PAGE_IDS";
        url = "sys/role/savePages.do";
        for (var i = 0, iLen = rows.length; i < iLen; i++) {
            param_value.push(rows[i][ID_NAME]);
        }
        params[PARAM_NAME] = param_value.join(",");

        saveDetail(url,params,getRolePages);
    }
    function saveDetail(url,params,successCallback) {
        $.ajax({
            url : url,
            dataType : 'html',
            async : true,
            data : params,
            success : function(data) {
                if (data) {
                    $.messager.alert('警告', '保存失败：' + data);
                }else{
                    $.messager.alert('提示', '保存成功！');
                    successCallback();
                }
            }
        });
    }
</script>
</html>
