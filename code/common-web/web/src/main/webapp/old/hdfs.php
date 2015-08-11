<?php
error_reporting(E_ERROR);
session_start();
$current_dir  = dirname(__FILE__);
$doc_root = $current_dir . '/';
require_once $doc_root . 'action/validate.php';
require_once $doc_root . 'dao/db.php';
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
    <meta charset="UTF-8">
    <title>队列监控</title>
    <link rel="stylesheet" type="text/css" href="css/easyui/themes/<?php echo isset( $_GET["theme"] )? $_GET["theme"] : "default"; ?>/easyui.css">
    <link rel="stylesheet" type="text/css" href="css/easyui/themes/icon.css">
    <link rel="stylesheet" type="text/css" href="css/main.css">
</head>
<body class="easyui-layout" data-options="fit:true">
    <div data-options="region:'center',border:true">
        <div id="divHDFSeContent" class="content-result">
        </div>
    </div>
    <script type="text/javascript" src="js/jquery.min.js"></script>
    <script type="text/javascript" src="js/easyui/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="js/easyui/locale/easyui-lang-zh_CN.js"></script>
    <script type="text/javascript" src="js/main.js"></script>
    
    <script type="text/javascript">
    $(function(){
       initHDFS();
    });
    function getModuleUrl (moduleId) {
        if(window.top.getModuleUrl) {
            getModuleUrl = window.top.getModuleUrl;
            return getModuleUrl(moduleId);
        }
    }
    function initHDFS () {
        var hdfsUrl = getModuleUrl(10108),datas = [];
        if (hdfsUrl) datas = getAjaxData('action/action.php',{t:'hdfs_path',url:hdfsUrl});
        $('#divHDFSeContent').datagrid({
            width: 1185,
            height: 435,
            method: 'GET',
            //pageSize: 15,
            pageNumber:1,
            //pageList : [15,30,50,100],
            fitColumns: true,
            autoRowHeight: true,
            //pagination: true,
            striped: true,
            rownumbers: true,
            data: datas,
            //url: 'action/action.php',
            singleSelect: true,
            scrollbarSize : 10,
            columns:[[
                {field:'path',title:'目录',halign:'center',align:'center'},
                {field:'directoryCount',title:'目录个数',halign:'center',align:'center'},
                {field:'fileCount',title:'文件个数',halign:'center',align:'center'},
                {field:'length',title:'目录大小',halign:'center',align:'center',formatter:byteFormatter2},
                {field:'quota',title:'名称空间配额',halign:'center',align:'center',formatter:quotaFormatter},
                {field:'spaceConsumed',title:'占用磁盘空间',halign:'center',align:'center',formatter:byteFormatter2},
                {field:'spaceQuota',title:'磁盘空间配额',halign:'center',align:'center',formatter:quotaFormatter}
            ]]
            //,rowStyler: serviceRowStyler
        });
    }
    function byteFormatter2(value,row,index){
        return byteFormatter(undefined,undefined,value);
    }
    function quotaFormatter(value,row,index){
        if (value == -1) {
            return '无限制';
        }else {
            return byteFormatter2(value,row,index);
        }
    }
       
    </script>
</body>
</html>