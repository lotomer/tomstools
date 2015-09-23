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
    <div id="divContainMain" style="width:100%;height:100%; overflow:auto">
    </div>
</body>
    <script type="text/javascript" src="js/jquery.min.js"></script>
    <script type="text/javascript" src="js/easyui/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="js/easyui/locale/easyui-lang-zh_CN.js"></script>
    <script type="text/javascript" src="js/utils.js"></script>
    <script type="text/javascript" src="js/index.js"></script>
    <script type="text/javascript">
        var theme = '${theme}',key='${user.key}',apiUrlPrefix='${user.configs.API_URL_PREFIX}',p='${p}',param='${page.params}';
        // 页面初始化
        $(function(){
            // 子页面
            subPages = eval('${subPages}');
            // 排序
            subPages = subPages.sort(function (content1,content2) {
                return content1.orderNum - content2.orderNum;
            });
            if (key) window.top.key=key;
            setTimeout("init()",300);
        });
        function init(){
        	// 按顺序创建子页面
            var maxWindow = subPages.length < 2;
            for (var i = 0,iLen = subPages.length; i < iLen; i++) {
                var subPage = subPages[i];
                createSubPage(subPage,true,undefined,true,maxWindow);
            }
        }
        // 根据page信息创建页面。如果page包含子页面，则使用子页面框架创建页面
        function createSubPage(page,isAppend,queryParams,canClose,maxWindow){
            //log(page);
            //alert(page.pageName + '\n' + page.contentURL);
            //return createModuleByContent("divContainMain",page.pageId,page.pageName,page.contentURL,page.pageId,page.pageName,page.params,page.width,page.height,isAppend,queryParams,canClose,false,theme,maxWindow);
            // 使用主页面的参数代替子页面的参数
            return createModuleByContent("divContainMain",page.pageId,page.pageName,page.contentURL,page.pageId,page.pageName,queryParams,page.width,page.height,isAppend,undefined,canClose,false,theme,maxWindow,undefined,page.autoFreshTime);
        }
        
    </script>
</html>
