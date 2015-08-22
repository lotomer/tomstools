<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>角色管理</title>
    <link rel="stylesheet" type="text/css" href='css/easyui/themes/${theme}/easyui.css'>
    <link rel="stylesheet" type="text/css" href="css/easyui/themes/icon.css">
    <link rel="stylesheet" type="text/css" href="css/main.css">
</head> 
<body class="easyui-layout">
    <div id="divRoleMgrMain" style="width:100%;height:100%; overflow:auto">
    </div>
</body>
    <script type="text/javascript" src="js/jquery.min.js"></script>
    <script type="text/javascript" src="js/easyui/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="js/easyui/locale/easyui-lang-zh_CN.js"></script>
    <script type="text/javascript" src="js/utils.js"></script>
    <script type="text/javascript" src="js/index.js"></script>
    <script type="text/javascript">
        // 面板与内容之间的差值
        var theme = '${theme}',key='${user.key}',apiUrlPrefix='${user.configs.API_URL_PREFIX}';
        // 页面初始化
        $(function(){
            // 子页面
            subPages = eval('${subPages}');
            // 排序
            subPages = subPages.sort(function (content1,content2) {
                return content1.orderNum - content2.orderNum;
            });
            setTimeout("init()",300);
        });
        function init(){
        	// 按顺序创建子页面
           
        }
    </script>
</html>