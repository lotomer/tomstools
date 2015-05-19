<%@ page language="java" pageEncoding="utf-8"%>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>资讯快报</title>
    <link rel="stylesheet" type="text/css" href="css/easyui/themes/default/easyui.css">
    <link rel="stylesheet" type="text/css" href="css/easyui/themes/icon.css">
    <link rel="stylesheet" type="text/css" href="css/main.css">
    
</head>
<body class="easyui-layout" data-options="fit:true">
    <div id="divHosts" data-options="region:'west',collapsed:false,split:true" title="网站列表" style="width:220px">
        <div id="divHostList">
        </div>
    </div>
    <div data-options="region:'center',split:true" style="height:100%;width:100%">
        <div id="divAccordion" class="easyui-accordion" style="width:900px;height:530px;">
            <div title="最新数据">
                <div id="divHostLatest" style="width:880px"></div>
            </div>
            <div title="历史数据" >
                <div id="divHostOlder" style="width:880px"></div>
            </div>
        </div>
    </div>
    <div data-options="region:'east',collapsed:false,split:true" title="控制面板" style="width:220px">
        <div id="divControl" style="width:200px" >
        </div>
    </div>
    <script type="text/javascript" src="js/jquery.min.js"></script>
    <script type="text/javascript" src="js/easyui/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="js/easyui/locale/easyui-lang-zh_CN.js"></script>
    <script type="text/javascript" src="js/main.js"></script>
    
    <script type="text/javascript">
    $(function(){
    	doInitHost();
    });
    </script>
</body>
</html>