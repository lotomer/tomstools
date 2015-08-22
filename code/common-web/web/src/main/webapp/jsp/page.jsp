<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${page.pageName}</title>
    <link rel="stylesheet" type="text/css" href='css/easyui/themes/${theme}/easyui.css'>
    <link rel="stylesheet" type="text/css" href="css/easyui/themes/icon.css">
    <link rel="stylesheet" type="text/css" href="css/main.css">
</head> 
<body class="easyui-layout">
	<div id="divPage${page.pageId}" data-options="region:'center',split:true,onResize:resizeContent">
    </div>
</body>
    <script type="text/javascript" src="js/jquery.min.js"></script>
    <script type="text/javascript" src="js/easyui/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="js/easyui/locale/easyui-lang-zh_CN.js"></script>
    <script type="text/javascript" src="js/utils.js"></script>
    <script type="text/javascript" src="js/index.js"></script>
<script type="text/javascript">
var theme = '${theme}',key='${user.key}';
if (key) window.top.key=key;
//window.top.createPageById('${page.pageId}');
//createModuleByContent("divContainMain",page.pageId,page.pageName,page.contentURL,page.pageId,page.pageName,page.params,page.width,page.height,isAppend,queryParams,canClose,false,theme,maxWindow);
createModuleByContent('divPage${page.pageId}','${page.pageId}','${page.pageName}','${page.contentURL}','${page.pageId}','${page.pageName}','${page.params}','${page.width}','${page.height}',true,undefined,true,false,theme,false);
//window.top.createPage('divPage${page.pageId}','${page.pageId}','${page.pageName}','${page.subPageId}','${page.contentURL}','${page.params}','${page.width}','${page.height}',undefined,'${theme}',undefined,undefined,true,true);
</script>
</html>
