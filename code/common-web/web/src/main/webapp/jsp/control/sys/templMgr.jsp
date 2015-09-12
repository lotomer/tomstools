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
    width: 80px;
}

.fitem input {
    width: 180px;
}
</style>
</head>
<body class="easyui-layout">
    <div id="divStatWordsContent" data-options="region:'west',split:true"
        style="width: 440px;"></div>
    <div id="divContentDetail" data-options="region:'center',split:true">
    </div>
    <div id="dlg" class="easyui-dialog" data-options="modal:true"
        style="width: 340px; height: 250px; padding: 10px 10px" closed="true"
        buttons="#dlg-buttons">
        <!-- <div class="ftitle">智能词条</div> -->
        <form id="fm" method="post" novalidate>
            <input type="hidden" name="LANG_ID" id="LANG_ID" /> <input
                type="hidden" name="SITE_TYPE_ID" id="SITE_TYPE_ID" /> <input
                type="hidden" name="COUNTRY_CODE" id="COUNTRY_CODE" />
            <div class="fitem">
                <label>语言：</label> <input id="selLang" class="easyui-combobox"
                    required="required"></input>
            </div>
            <div class="fitem">
                <label>国家：</label> <input id="selCountry" class="easyui-combobox"
                    required="required"></input>
            </div>
            <div class="fitem">
                <label>站点类型：</label> <input id="selSiteType" class="easyui-combobox"
                    required="required"></input>
            </div>
            <div class="fitem">
                <label>站点名：</label> <input name="SITE_NAME" class="easyui-textbox"
                    required="required">
            </div>
            <div class="fitem">
                <label>域名：</label> <input name="SITE_HOST" class="easyui-textbox "
                    required="required">
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

    <div id="dlgDetail" class="easyui-dialog" data-options="modal:true"
        style="width: 340px; height: 220px; padding: 10px 10px" closed="true"
        buttons="#dlg-buttonsDetail">
        <!-- <div class="ftitle">智能词条</div> -->
        <form id="fmDetail" method="post" novalidate>
            <input type="hidden" name="SITE_ID" id="SITE_ID" />
            <div class="fitem">
                <label>站点名称：</label> <input name="SITE_NAME" class="easyui-textbox"
                    required="required" data-options="disabled: true"></input>
            </div>
            <div class="fitem">
                <label>URL地址：</label> <input name="URL" class="easyui-textbox"
                    style="height: 80px" required="required"
                    data-options="multiline:true"></input>
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
    var theme = '${theme}', key = '${user.key}',ID_KEY='MENU_ID',URL_PREFIX='sys/menu',DETAIL_URL_PREFIX='sys/page',NAME='菜单',DETAIL_NAME='页面';
    // 页面初始化
    $(function() {
        // 绑定事件
        //query();
    	initCombobox("selCountry", "crawl/country.do", function(record) {
			if (record != undefined && record.id != undefined)
				$('#COUNTRY_CODE').val(record.id);
		}, true);
    });
    
    var url;
    function add() {
        $('#dlg').dialog('open').dialog('setTitle', '新增' + NAME);
        $('#fm').form('clear');
        url = URL_PREFIX + '/add.do?key=' + key;
    }
    function edit() {
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
                    $.post(URL_PREFIX + '/delete.do', {
                        id : row[ID_KEY],
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
            title : NAME + '列表',
            url : URL_PREFIX + '/select.do',
            fitColumns : true,
            rownumbers : true,
            singleSelect : true,
            remoteSort : false,
            idField : ID_KEY,
            //sortName : "status",
            //sortOrder : "asc",
            pagination : true,
            pageSize : pageSize,
            pageList : getPageList(pageSize),
            onSelect : function(index, row) {
                /*siteData = row;
                $('#divMetricDetail').datagrid({
                    title : "待爬取URL列表【" + row.SITE_NAME + "】"
                }).datagrid("load", {
                    key : key,
                    SITE_ID : row.SITE_ID
                });
                $('#divMetricDetail').datagrid("unselectAll");*/
            },
            columns : [ [ {
                field : 'SITE_NAME',
                title : '站点名',
                align : 'center',
                halign : 'center'
            }, {
                field : 'SITE_HOST',
                title : '站点域名',
                align : 'center',
                halign : 'center'
            }, {
                field : 'SITE_TYPE_NAME',
                title : '站点类型',
                align : 'center',
                halign : 'center'
            }, {
                field : 'LANG_NAME',
                title : '语言',
                align : 'center',
                halign : 'center',
                sortable : "true"
            }, {
                field : 'COUNTRY_NAME',
                title : '区域',
                align : 'center',
                halign : 'center'
            } ] ]
        }).datagrid('clientPaging');
    }

    var urlDetail;
    function addDetail() {
        if (siteData == undefined)
            return;
        $('#dlgDetail').dialog('open').dialog('setTitle', '新增' + DETAIL_NAME);
        $('#fmDetail').form('clear').form("load", siteData);
        urlDetail = DETAIL_URL_PREFIX + '/add.do?key=' + key;
    }
    function editDetail() {
        if (siteData == undefined)
            return;
        var row = $('#divMetricDetail').datagrid('getSelected');
        if (row) {
            $('#dlgDetail').dialog('open').dialog('setTitle', '修改' + DETAIL_NAME);
            $('#fmDetail').form("clear").form("load", siteData).form('load',
                    row);
            urlDetail =DETAIL_URL_PREFIX + '/save.do?key=' + key + '&id='
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

    /**
     * 执行查询
     */
    function queryDetail() {
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
            title : "待爬取URL列表【所有站点】",
            url : "setting/siteDetail/select.do",
            queryParams : params,
            fitColumns : true,
            rownumbers : true,
            singleSelect : true,
            remoteSort : false,
            idField : "ID",
            //sortName : "status",
            //sortOrder : "asc",
            pagination : true,
            pageSize : pageSize,
            pageList : getPageList(pageSize),
            columns : [ [  {
                field : 'SITE_NAME',
                title : '站点名',
                align : 'center',
                halign : 'center'
            } ,{
                field : 'URL',
                title : '待爬取URL',
                align : 'left',
                halign : 'center'
            } ] ]
        }).datagrid('clientPaging');
    }
</script>
</html>
