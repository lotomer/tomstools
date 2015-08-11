<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${metricInfo.title}</title>
    <link rel="stylesheet" type="text/css" href='css/easyui/themes/${theme}/easyui.css'>
    <link rel="stylesheet" type="text/css" href="css/easyui/themes/icon.css">
    <link rel="stylesheet" type="text/css" href="css/main.css">
</head> 
<body class="easyui-layout">
    <div id="divMetric_${metricInfo.id}" style="width:100%;height:100%">
        ${metricInfo.templateContent}
    </div>
</body>
<!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
  <script src="js/echarts/html5shiv.min.js"></script>
  <script src="js/echarts/respond.min.js"></script>
<![endif]-->
<script type="text/javascript" src="js/echarts/echarts.js"></script>
<script type="text/javascript" src="js/jquery.min.js"></script>
<script type="text/javascript" src="js/easyui/jquery.easyui.min.js"></script>
<script type="text/javascript" src="js/easyui/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/index.js"></script>
<script type="text/javascript" src="js/myEcharts.js"></script>
<script type="text/javascript">
// 面板与内容之间的差值
var theme = '${theme}',key='${user.key}',apiUrlPrefix='${user.configs.API_URL_PREFIX}';

// 页面初始化
$(function(){
    if (key) window.top.key=key;
	showLoading("divMetric_${metricInfo.id}");
	setTimeout("initMetric()",200);
	
});
function initMetric() {
	var divMetric = $('#divMetric_${metricInfo.id}');
    //showLoading('divMetric_${metricInfo.id}');
    ${metricInfo.templateScript}
}
</script>
</html>
